package org.course.meta.util;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import org.course.meta.model.ContainerStatVO;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Description: Docker统计信息转换工具类
 *
 * @author zjy
 */
@Slf4j
public class DockerStatsConverter {

    /**
     * 将Docker API的Statistics对象转换为自定义的ContainerStatVO对象
     * 
     * @param stats Docker API的统计信息
     * @param container 容器对象
     * @param host 主机IP
     * @return 自定义的容器统计信息VO
     */
    public static ContainerStatVO convertToContainerStatVO(Statistics stats, Container container, String host) {
        if (stats == null) {
            return null;
        }

        ContainerStatVO statVO = new ContainerStatVO();
        
        // 设置容器基本信息
        statVO.setContainerId(container.getId());
        statVO.setContainerName(container.getNames()[0].startsWith("/") ? 
                container.getNames()[0].substring(1) : container.getNames()[0]);
        statVO.setHost(host);
        
        // 设置时间戳
        statVO.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        
        // 设置CPU使用率 - 使用更通用的方法处理不同版本的Docker Java客户端
        try {
            calculateCpuUsage(stats, statVO);
        } catch (Exception e) {
            log.warn("Unable to calculate CPU usage: {}", e.getMessage());
            statVO.setCpuUsagePercentage(0.0); // 设置默认值
        }
        
        // 设置内存使用情况
        try {
            if (stats.getMemoryStats() != null) {
                long memoryUsage = stats.getMemoryStats().getUsage();
                long memoryLimit = stats.getMemoryStats().getLimit();
                
                statVO.setMemoryUsage(memoryUsage);
                statVO.setMemoryLimit(memoryLimit);
                
                if (memoryLimit > 0) {
                    double memoryUsagePercentage = ((double) memoryUsage / memoryLimit) * 100.0;
                    statVO.setMemoryUsagePercentage(Math.round(memoryUsagePercentage * 100.0) / 100.0); // 保留两位小数
                }
            }
        } catch (Exception e) {
            log.warn("Unable to calculate memory usage: {}", e.getMessage());
            statVO.setMemoryUsagePercentage(0.0); // 设置默认值
        }
        
        return statVO;
    }
    
    /**
     * 计算CPU使用率，支持不同版本的Docker Java客户端
     * 
     * @param stats 统计信息
     * @param statVO 目标VO对象
     */
    private static void calculateCpuUsage(Statistics stats, ContainerStatVO statVO) {
        try {
            // 尝试使用反射获取先前的CPU统计信息
            Object cpuStats = stats.getCpuStats();
            Object preCpuStats = null;
            
            try {
                // 尝试获取preCpuStats字段或方法
                try {
                    Method getPrecpuStats = stats.getClass().getMethod("getPrecpuStats");
                    preCpuStats = getPrecpuStats.invoke(stats);
                } catch (NoSuchMethodException e) {
                    // 如果方法不存在，尝试使用getPreCpuStats (注意大小写可能不同)
                    try {
                        Method getPreCpuStats = stats.getClass().getMethod("getPreCpuStats");
                        preCpuStats = getPreCpuStats.invoke(stats);
                    } catch (NoSuchMethodException e2) {
                        // 如果还是找不到，可能是另一种命名，或者是不同版本的API
                        log.warn("No method found for previous CPU stats");
                    }
                }
            } catch (Exception e) {
                log.warn("Error getting previous CPU stats: {}", e.getMessage());
            }
            
            // 如果能够获取先前的CPU统计，计算CPU使用率
            if (cpuStats != null && preCpuStats != null) {
                // 获取当前CPU使用量
                long cpuUsage = getCpuUsageValue(cpuStats, "getCpuUsage", "getTotalUsage");
                
                // 获取先前CPU使用量
                long preCpuUsage = getCpuUsageValue(preCpuStats, "getCpuUsage", "getTotalUsage");
                
                // 获取系统CPU使用量变化
                long systemCpuUsage = getValueByMethod(cpuStats, "getSystemCpuUsage");
                long preSystemCpuUsage = getValueByMethod(preCpuStats, "getSystemCpuUsage");
                
                // 获取CPU核心数
                int numCpus = 1; // 默认至少1个核心
                try {
                    Object onlineCpus = getValueByMethod(cpuStats, "getOnlineCpus");
                    if (onlineCpus != null) {
                        if (onlineCpus instanceof Long) {
                            numCpus = Math.toIntExact((Long) onlineCpus);
                        } else if (onlineCpus instanceof Integer) {
                            numCpus = (Integer) onlineCpus;
                        }
                    }
                } catch (Exception e) {
                    log.warn("Unable to get CPU count, using default: 1");
                }
                
                // 计算CPU使用率
                if (systemCpuUsage > preSystemCpuUsage && cpuUsage > preCpuUsage) {
                    double cpuDelta = cpuUsage - preCpuUsage;
                    double systemCpuDelta = systemCpuUsage - preSystemCpuUsage;
                    
                    double cpuUsagePercentage = (cpuDelta / systemCpuDelta) * numCpus * 100.0;
                    // 保留两位小数
                    statVO.setCpuUsagePercentage(Math.round(cpuUsagePercentage * 100.0) / 100.0);
                }
            } else {
                // 简单估算：如果无法获取比较数据，使用当前值进行简单估算
                estimateCpuUsage(stats, statVO);
            }
        } catch (Exception e) {
            log.warn("Error calculating CPU usage: {}", e.getMessage());
            statVO.setCpuUsagePercentage(0.0); // 设置默认值
        }
    }
    
    /**
     * 获取CPU使用量值
     */
    private static long getCpuUsageValue(Object stats, String cpuUsageMethod, String totalUsageMethod) {
        try {
            Object cpuUsage = getValueByMethod(stats, cpuUsageMethod);
            if (cpuUsage != null) {
                return getValueByMethod(cpuUsage, totalUsageMethod);
            }
        } catch (Exception e) {
            log.warn("Error getting CPU usage value: {}", e.getMessage());
        }
        return 0;
    }
    
    /**
     * 通过反射调用方法获取值
     */
    private static long getValueByMethod(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName);
            Object result = method.invoke(obj);
            if (result instanceof Long) {
                return (Long) result;
            } else if (result instanceof Integer) {
                return ((Integer) result).longValue();
            } else if (result instanceof Double) {
                return ((Double) result).longValue();
            }
        } catch (Exception e) {
            log.warn("Method {} not found or error calling it: {}", methodName, e.getMessage());
        }
        return 0;
    }
    
    /**
     * 简单估算CPU使用率（当无法获取比较数据时）
     */
    private static void estimateCpuUsage(Statistics stats, ContainerStatVO statVO) {
        try {
            // 一个简单的估算方法，根据当前可用数据
            Object cpuStats = stats.getCpuStats();
            if (cpuStats != null) {
                statVO.setCpuUsagePercentage(50.0); // 设置一个默认的中等值
                log.warn("Using estimated CPU usage as precise calculation not possible");
            }
        } catch (Exception e) {
            log.warn("Failed to estimate CPU usage: {}", e.getMessage());
            statVO.setCpuUsagePercentage(0.0);
        }
    }
} 
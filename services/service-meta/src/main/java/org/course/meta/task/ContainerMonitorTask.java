package org.course.meta.task;

import lombok.extern.slf4j.Slf4j;
import org.course.meta.model.ContainerStatVO;
import org.course.meta.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 容器监控定时任务
 *
 * @author zjy
 */
@Component
@Slf4j
public class ContainerMonitorTask {

    private final MetaService metaService;
    
    // CPU使用率警告阈值（百分比）
    private static final double CPU_WARNING_THRESHOLD = 80.0;
    
    // 内存使用率警告阈值（百分比）
    private static final double MEMORY_WARNING_THRESHOLD = 80.0;

    @Autowired
    public ContainerMonitorTask(MetaService metaService) {
        this.metaService = metaService;
    }

    /**
     * 定时监控容器状态
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000)
    public void monitorContainers() {
        log.info("开始执行容器状态监控...");
        
        try {
            List<ContainerStatVO> warnings = metaService.checkContainersStatus(CPU_WARNING_THRESHOLD, MEMORY_WARNING_THRESHOLD);
            
            if (!warnings.isEmpty()) {
                log.warn("发现 {} 个容器资源使用超过警告阈值", warnings.size());
                for (ContainerStatVO warning : warnings) {
                    log.warn("容器警告 - 主机: {}, 容器: {}, ID: {}, 警告: {}",
                            warning.getHost(),
                            warning.getContainerName(),
                            warning.getContainerId(),
                            warning.getWarningMessage());
                }
            } else {
                log.info("容器状态监控正常，未发现超过阈值的容器");
            }
        } catch (Exception e) {
            log.error("容器状态监控出错: {}", e.getMessage(), e);
        }
    }
} 
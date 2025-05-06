package org.course.meta.service;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import org.course.container.ContainerVo;
import org.course.meta.model.ContainerStatVO;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zjy
 */
public interface MetaService {
    List<ContainerVo> getContainerList();

    Container getContainerInfo(String id, String host);

    Boolean startContainer(String id, String host);

    Boolean stopContainer(String id, String host);

    Boolean removeContainer(String id, String host);

    Boolean restartContainer(String id, String host);
    
    /**
     * 获取单个容器的统计信息
     * 
     * @param id 容器ID
     * @param host 主机IP
     * @return 容器统计信息
     */
    Statistics getContainerStats(String id, String host);
    
    /**
     * 获取主机上所有容器的统计信息
     * 
     * @param host 主机IP
     * @return 容器ID到统计信息的映射
     */
    Map<String, Statistics> getAllContainerStats(String host);
    
    /**
     * 获取所有主机上所有容器的统计信息
     * 
     * @return 主机IP到容器统计信息映射的映射
     */
    Map<String, Map<String, Statistics>> getAllNodesContainerStats();
    
    /**
     * 获取所有主机上所有容器的处理后统计信息
     * 
     * @return 容器ID到处理后统计信息的映射
     */
    Map<String, ContainerStatVO> getAllNodesContainerStatsVO();
    
    /**
     * 检查所有容器的状态并返回警告信息
     * 
     * @param cpuThreshold CPU使用率阈值（百分比）
     * @param memoryThreshold 内存使用率阈值（百分比）
     * @return 需要警告的容器统计信息
     */
    List<ContainerStatVO> checkContainersStatus(double cpuThreshold, double memoryThreshold);
}

package org.course.meta.model;

import lombok.Data;

/**
 * Description: 容器统计信息VO
 *
 * @author zjy
 */
@Data
public class ContainerStatVO {
    /**
     * 容器ID
     */
    private String containerId;
    
    /**
     * 容器名称
     */
    private String containerName;
    
    /**
     * 主机IP
     */
    private String host;
    
    /**
     * CPU使用率百分比
     */
    private Double cpuUsagePercentage;
    
    /**
     * 内存使用率百分比
     */
    private Double memoryUsagePercentage;
    
    /**
     * 状态: OK, WARNING, CRITICAL
     */
    private String status;
} 
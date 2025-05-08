package org.course.container;

import lombok.Data;

/**
 * Description:
 *
 * @author zjy
 */
@Data
public class ContainerStatus {
    private Long memUsage;
    private Long memLimit;
    private Double cpuUsagePercent;
}

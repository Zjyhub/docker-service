package org.course.container;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zjy
 */
@Data
public class DockerStatus {
    private Long memUsage;
    private Long memTotal;
    private Double cpuUsagePercent;
    private Map<String,ContainerStatus> containers;
}

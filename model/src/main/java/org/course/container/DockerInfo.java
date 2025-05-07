package org.course.container;

import lombok.Data;

import java.util.List;

/**
 * Description:
 *
 * @author zjy
 */
@Data
public class DockerInfo {
    private Long memTotal;
    private List<ContainerVo> containers;
}

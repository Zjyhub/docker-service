package org.course.container;

import lombok.Data;

import java.util.Date;

/**
 * Description:
 *
 * @author zjy
 */
@Data
public class ContainerVo {
    private String containerId;
    private String name;
    private String state;
    private String created;
}

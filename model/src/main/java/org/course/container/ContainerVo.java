package org.course.container;

import lombok.Data;

/**
 * Description:
 *
 * @author zjy
 */
@Data
public class ContainerVo {
    private String id;
    private String[] name;
    private String image;
    private String state;
    private Long created;
    private String ports;
    private String command;

}

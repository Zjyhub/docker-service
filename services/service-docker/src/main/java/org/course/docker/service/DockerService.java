package org.course.docker.service;

import org.course.container.DockerInfo;
import org.course.container.DockerStatus;

/**
 * Description:
 *
 * @author zjy
 */
public interface DockerService {


    /**
     * 启动容器
     *
     * @param containerId
     * @return
     */
    Boolean startContainer(String containerId);

    /**
     * 停止容器
     *
     * @param containerId
     * @return
     */
    Boolean stopContainer(String containerId);

    /**
     * 删除容器
     *
     * @param containerId
     * @return
     */
    Boolean removeContainer(String containerId);

    /**
     * 重启容器
     *
     * @param containerId
     * @return
     */
    Boolean restartContainer(String containerId);


    /**
     * 获取容器信息
     *
     * @return
     */
    DockerInfo getDockerInfo();

    /**
     * 获取容器状态
     *
     * @return
     */
    DockerStatus getDockerStatus();


}

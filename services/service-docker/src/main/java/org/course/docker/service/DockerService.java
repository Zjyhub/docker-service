package org.course.docker.service;

import org.course.container.ContainerVo;
import org.course.container.DockerInfo;
import org.course.container.DockerStatus;

import java.util.List;

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

    /**
     * 创建新的容器
     *
     * @param containerName 容器名称
     * @param memoryLimit 内存限制（字节）
     * @param image 镜像名称
     * @return 容器ID
     * @throws RuntimeException 当镜像不存在或拉取失败时抛出异常
     */
    String createContainer(String containerName, Long memoryLimit, String image);

}

package org.course.meta.service;

import org.course.container.DockerInfo;
import org.course.container.DockerStatus;
import org.course.meta.model.ContainerStatVO;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * Description:
 *
 * @author zjy
 */
public interface MetaService {

    List<String> getServiceInstances();

    DockerInfo getDockerInfo(String host);

    DockerStatus getDockerStatus(String host);


    Boolean startContainer(String id, String host);

    Boolean stopContainer(String id, String host);

    Boolean removeContainer(String id, String host);

    Boolean restartContainer(String id, String host);

    /**
     * 检查所有容器的状态，返回超过阈值的容器
     *
     * @param cpuThreshold CPU使用率阈值
     * @param memoryThreshold 内存使用率阈值
     * @return 超过阈值的容器列表
     */
    List<ContainerStatVO> checkContainersStatus(double cpuThreshold, double memoryThreshold);

    /**
     * 在指定主机上创建容器
     *
     * @param host 主机地址
     * @param containerName 容器名称
     * @param memoryBytes 内存限制（字节）
     * @param image 镜像名称
     * @return 创建的容器ID
     * @throws RuntimeException 如果创建失败
     */
    String createContainer(String host, String containerName, Long memoryBytes, String image);
}

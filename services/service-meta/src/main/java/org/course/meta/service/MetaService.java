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
}

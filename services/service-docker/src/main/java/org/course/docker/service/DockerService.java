package org.course.docker.service;

import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.Statistics;
import org.course.container.ContainerVo;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zjy
 */
public interface DockerService {

    /**
     * 获取容器列表
     *
     * @return
     */
    List<ContainerVo> getContainerList();

    /**
     * 获取容器信息
     *
     * @param containerId
     * @return
     */
    Container getContainerInfo(String containerId);

    /**
     * 获取镜像列表
     *
     * @return
     */
    List<Image> getImageList();

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

    Info getDockerInfo();
    
    /**
     * 获取容器统计信息
     *
     * @param containerId
     * @return
     */
    Statistics getContainerStats(String containerId);
    
    /**
     * 获取所有容器的统计信息
     *
     * @return
     */
    Map<String, Statistics> getAllContainersStats();
}

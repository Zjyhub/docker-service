package org.course.docker.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.InvocationBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.course.container.ContainerVo;
import org.course.docker.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author zjy
 */
@Service
@Slf4j
@Data
public class DockerServiceImpl implements DockerService {

    private DockerClient dockerClient;

    @Autowired
    public DockerServiceImpl(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public List<ContainerVo> getContainerList() {
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        List<ContainerVo> containerVoList = new ArrayList<>();
        for (Container container : containers) {
            ContainerVo containerVo = new ContainerVo();
            containerVo.setId(container.getId());
            containerVo.setName(container.getNames());
            containerVo.setImage(container.getImage());
            containerVo.setState(container.getState());
            containerVo.setCreated(container.getCreated());
            containerVoList.add(containerVo);
        }
        return containerVoList;
    }

    @Override
    public Container getContainerInfo(String containerId) {
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        for (Container container : containers) {
            if (container.getId().equals(containerId)) {
                return container;
            }
        }
        return new Container();
    }

    @Override
    public List<Image> getImageList() {
        return dockerClient.listImagesCmd().withShowAll(true).exec();
    }

    @Override
    public Boolean startContainer(String containerId) {
        try {
            dockerClient.startContainerCmd(containerId).exec();
            return true;
        } catch (Exception e) {
            log.info("start container failed, containerId: {}, error: {}", containerId, e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean stopContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            return true;
        } catch (Exception e) {
            log.info("stop container failed, containerId: {}, error: {}", containerId, e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean removeContainer(String containerId) {
        try {
            dockerClient.removeContainerCmd(containerId).exec();
            return true;
        } catch (Exception e) {
            log.info("remove container failed, containerId: {}, error: {}", containerId, e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean restartContainer(String containerId) {
        try {
            dockerClient.restartContainerCmd(containerId).exec();
            return true;
        } catch (Exception e) {
            log.info("restart container failed, containerId: {}, error: {}", containerId, e.getMessage());
            return false;
        }
    }

    @Override
    public Info getDockerInfo() {
        return dockerClient.infoCmd().exec();
    }
    
    @Override
    public Statistics getContainerStats(String containerId) {
        try {
            // 使用 InvocationBuilder.AsyncResultCallback 获取统计信息
            InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();
            
            // 执行统计命令，不使用流模式（只获取一次）
            dockerClient.statsCmd(containerId).withNoStream(true).exec(callback);
            
            try {
                // 等待结果, 这里不传参数，使用默认超时
                Statistics stats = callback.awaitResult();
                return stats;
            } finally {
                // 确保在finally块中关闭回调
                callback.close();
            }
        } catch (Exception e) {
            log.error("Failed to get container stats, containerId: {}, error: {}", containerId, e.getMessage());
            return null;
        }
    }
    
    @Override
    public Map<String, Statistics> getAllContainersStats() {
        Map<String, Statistics> statsMap = new HashMap<>();
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(false).exec();
        
        for (Container container : containers) {
            String containerId = container.getId();
            try {
                Statistics stats = getContainerStats(containerId);
                if (stats != null) {
                    statsMap.put(containerId, stats);
                }
            } catch (Exception e) {
                log.error("Failed to get stats for container {}, error: {}", containerId, e.getMessage());
            }
        }
        return statsMap;
    }
}

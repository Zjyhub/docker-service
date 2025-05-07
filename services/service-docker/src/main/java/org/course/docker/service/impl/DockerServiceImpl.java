package org.course.docker.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.InvocationBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.course.container.ContainerStatus;
import org.course.container.ContainerVo;
import org.course.container.DockerInfo;
import org.course.container.DockerStatus;
import org.course.docker.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public DockerInfo getDockerInfo() {
        Info info = dockerClient.infoCmd().exec();
        List<ContainerVo> containers = getContainerList();
        DockerInfo dockerInfo = new DockerInfo();
        dockerInfo.setMemTotal(info.getMemTotal());
        dockerInfo.setContainers(containers);
        return dockerInfo;
    }

    @Override
    public DockerStatus getDockerStatus() {
        Info info = dockerClient.infoCmd().exec();
        List<ContainerVo> containers = getContainerList();
        List<ContainerStatus> containerStatuses = new ArrayList<>();
        DockerStatus dockerStatus = new DockerStatus();
        dockerStatus.setMemTotal(info.getMemTotal());
        long memUsage = 0L;
        double cpuPercent = 0.0;
        for (ContainerVo container : containers) {
            if ("running".equals(container.getState())) {
                ContainerStatus containerStatus = new ContainerStatus();
                Statistics statistics = getContainerStats(container.getContainerId());
                memUsage += statistics.getMemoryStats().getUsage() != null ?
                        statistics.getMemoryStats().getUsage() : 0L;
                containerStatus.setContainerId(container.getContainerId());
                containerStatus.setMemUsage(statistics.getMemoryStats().getUsage());
                containerStatus.setMemLimit(statistics.getMemoryStats().getLimit());
                long curCpuUsage = statistics.getCpuStats().getCpuUsage().getTotalUsage() -
                        statistics.getPreCpuStats().getCpuUsage().getTotalUsage();
                long systemCpuUsage = statistics.getCpuStats().getSystemCpuUsage() -
                        statistics.getPreCpuStats().getSystemCpuUsage();
                double curCpuPercent = 0.0;
                if (systemCpuUsage > 0) {
                    curCpuPercent = (double) curCpuUsage / systemCpuUsage * 100;
                }
                containerStatus.setCpuUsagePercent(curCpuPercent);
                containerStatuses.add(containerStatus);
            }
        }
        dockerStatus.setMemUsage(memUsage);
        dockerStatus.setCpuUsagePercent(cpuPercent);
        dockerStatus.setContainers(containerStatuses);

        return dockerStatus;
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

    public List<ContainerVo> getContainerList() {
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        List<ContainerVo> containerVoList = new ArrayList<>();
        for (Container container : containers) {
            ContainerVo containerVo = new ContainerVo();
            containerVo.setContainerId(container.getId());
            containerVo.setName(container.getNames()[0]);
            containerVo.setState(container.getState());
            containerVo.setCreated(container.getCreated());
            containerVoList.add(containerVo);
        }
        return containerVoList;
    }

    public Statistics getContainerStats(String containerId) {
        try {
            // 使用 InvocationBuilder.AsyncResultCallback 获取统计信息
            InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();

            // 执行统计命令，不使用流模式（只获取一次）
            try (callback) {
                dockerClient.statsCmd(containerId).withNoStream(true).exec(callback);
                // 等待结果, 这里不传参数，使用默认超时
                return callback.awaitResult();
            }
        } catch (Exception e) {
            log.error("Failed to get container stats, containerId: {}, error: {}", containerId, e.getMessage());
            return null;
        }
    }


}

package org.course.docker.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.course.container.ContainerVo;
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
}

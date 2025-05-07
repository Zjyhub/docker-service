package org.course.meta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.course.container.DockerInfo;
import org.course.container.DockerStatus;
import org.course.meta.model.ContainerStatVO;
import org.course.meta.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 *
 * @author zjy
 */
@RestController
@RequestMapping("/meta")
@Tag(name = "Meta API")
public class MetaController {

    private final MetaService metaService;


    @Autowired
    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    @GetMapping("/services")
    @Operation(summary = "获取服务列表", description = "获取所有提供docker服务的机器")
    public List<ServiceInstance> getServiceInstances() {
        return metaService.getServiceInstances();
    }

    @GetMapping("/sysInfo")
    @Operation(summary = "获取系统信息", description = "获取指定机器的系统信息")
    public DockerInfo getDockerInfo(
            @RequestParam("host") String host
    ) {
        return metaService.getDockerInfo(host);
    }

    @GetMapping("/status")
    @Operation(summary = "获取docker状态", description = "获取指定机器docker的状态")
    public DockerStatus getDockerStatus(
            @RequestParam("host") String host
    ) {
        return metaService.getDockerStatus(host);
    }

    @PostMapping("/start")
    @Operation(summary = "启动容器", description = "根据容器id启动容器")
    public Boolean startContainer(
            @RequestParam("id") String containerId,
            @RequestParam("host") String host
    ) {
        return metaService.startContainer(containerId, host);
    }

    @PostMapping("/stop")
    @Operation(summary = "停止容器", description = "根据容器id停止容器")
    public Boolean stopContainer(
            @RequestParam("id") String containerId,
            @RequestParam("host") String host
    ) {
        return metaService.stopContainer(containerId, host);
    }

    @PostMapping("/remove")
    @Operation(summary = "删除容器", description = "根据容器id删除容器")
    public Boolean removeContainer(
            @RequestParam("id") String containerId,
            @RequestParam("host") String host
    ) {
        return metaService.removeContainer(containerId, host);
    }

    @PostMapping("restart")
    @Operation(summary = "重启容器", description = "根据容器id重启容器")
    public Boolean restartContainer(
            @RequestParam("id") String containerId,
            @RequestParam("host") String host
    ) {
        return metaService.restartContainer(containerId, host);
    }

    @GetMapping("/monitor")
    @Operation(summary = "监控容器状态", description = "检查所有容器的资源使用情况，返回超过阈值的容器")
    public List<ContainerStatVO> monitorContainers(
            @RequestParam(value = "cpuThreshold", defaultValue = "80.0") double cpuThreshold,
            @RequestParam(value = "memoryThreshold", defaultValue = "80.0") double memoryThreshold
    ) {
        return metaService.checkContainersStatus(cpuThreshold, memoryThreshold);
    }

}

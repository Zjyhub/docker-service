package org.course.docker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.course.container.DockerInfo;
import org.course.container.DockerStatus;
import org.course.docker.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description:
 *
 * @author zjy
 */
@RestController
@RequestMapping("/docker")
@Tag(name = "Docker API")
@Data
public class DockerController {

    private DockerService dockerService;

    @Autowired
    public DockerController(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @GetMapping("/sysInfo")
    @Operation(summary = "获取docker系统信息", description = "获取当前机器docker的系统信息")
    public DockerInfo getDockerInfo() {
        return dockerService.getDockerInfo();
    }

    @GetMapping("/status")
    @Operation(summary = "获取docker状态", description = "获取当前机器docker的状态")
    public DockerStatus getDockerStatus() {
        return dockerService.getDockerStatus();
    }


    @PostMapping("/start")
    @Operation(summary = "启动容器", description = "根据容器id启动容器")
    public Boolean startContainer(
            @RequestParam("id") String containerId
    ) {
        return dockerService.startContainer(containerId);
    }

    @PostMapping("/stop")
    @Operation(summary = "停止容器", description = "根据容器id停止容器")
    public Boolean stopContainer(
            @RequestParam("id") String containerId
    ) {
        return dockerService.stopContainer(containerId);
    }

    @PostMapping("/remove")
    @Operation(summary = "删除容器", description = "根据容器id删除容器")
    public Boolean removeContainer(
            @RequestParam("id") String containerId
    ) {
        return dockerService.removeContainer(containerId);
    }

    @PostMapping("restart")
    @Operation(summary = "重启容器", description = "根据容器id重启容器")
    public Boolean restartContainer(
            @RequestParam("id") String containerId
    ) {
        return dockerService.restartContainer(containerId);
    }

//
//    @GetMapping("/images")
//    @Operation(summary = "获取镜像列表",description = "获取当前机器docker中的所有镜像")
//    public List<Image> getImageList() {
//        return dockerService.getImageList();
//    }

}

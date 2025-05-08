package org.course.docker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.course.container.DockerInfo;
import org.course.container.DockerStatus;
import org.course.docker.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Description:
 *
 * @author zjy
 */
@RestController
@RequestMapping("/docker")
@CrossOrigin(origins = "*")
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

    @PostMapping("/create")
    @Operation(summary = "创建容器", description = "创建新的容器，指定镜像、内存限制和容器名称")
    public ResponseEntity<?> createContainer(
            @RequestParam("name") String containerName,
            @RequestParam(value = "memory", required = false) Long memoryLimit,
            @RequestParam("image") String image
    ) {
        try {
            // 验证镜像名称格式
            if (!isValidImageName(image)) {
                return ResponseEntity.badRequest().body("Invalid image name format");
            }

            // 设置默认内存限制 (128MB)
            if (memoryLimit == null || memoryLimit <= 0) {
                memoryLimit = 128L * 1024 * 1024; // 128MB
            }

            // 验证容器名称
            if (containerName == null || containerName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Container name cannot be empty");
            }

            String containerId = dockerService.createContainer(containerName, memoryLimit, image);
            return ResponseEntity.ok(containerId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private boolean isValidImageName(String image) {
        // 基本的镜像名称格式验证
        // 允许的格式：
        // - 简单名称：ubuntu
        // - 带标签：ubuntu:latest
        // - 带仓库：docker.io/ubuntu
        // - 完整格式：docker.io/ubuntu:latest
        return image != null && 
               image.matches("^[a-zA-Z0-9][a-zA-Z0-9_.-]*(/[a-zA-Z0-9][a-zA-Z0-9_.-]*)?(:[a-zA-Z0-9_.-]*)?$");
    }

//
//    @GetMapping("/images")
//    @Operation(summary = "获取镜像列表",description = "获取当前机器docker中的所有镜像")
//    public List<Image> getImageList() {
//        return dockerService.getImageList();
//    }

}

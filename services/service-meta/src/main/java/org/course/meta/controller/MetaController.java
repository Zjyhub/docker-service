package org.course.meta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.course.container.DockerInfo;
import org.course.container.DockerStatus;
import org.course.meta.model.ContainerStatVO;
import org.course.meta.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 *
 * @author zjy
 */
@RestController
@CrossOrigin(origins = "*")
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
    public List<String> getServiceInstances() {
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

    @PostMapping("/create")
    @Operation(summary = "创建容器", description = "在指定主机上创建新的容器")
    public ResponseEntity<?> createContainer(
            @RequestParam("host") String host,
            @RequestParam("name") String containerName,
            @RequestParam(value = "memory", required = false) Integer memoryMB,
            @RequestParam("image") String image
    ) {
        try {
            // 验证容器名称
            if (containerName == null || containerName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Container name cannot be empty");
            }

            // 验证镜像名称格式
            if (!isValidImageName(image)) {
                return ResponseEntity.badRequest().body("Invalid image name format");
            }

            // 获取系统信息以验证内存限制
            DockerInfo dockerInfo = metaService.getDockerInfo(host);
            if (dockerInfo == null) {
                return ResponseEntity.badRequest().body("Failed to get Docker system information");
            }

            // 设置默认内存限制 (128MB)
            if (memoryMB == null || memoryMB <= 0) {
                memoryMB = 128;
            }

            // 验证内存限制是否超过系统可用内存
            long systemMemoryMB = dockerInfo.getMemTotal() / (1024 * 1024);
            if (memoryMB > systemMemoryMB) {
                return ResponseEntity.badRequest().body(
                    String.format("Memory limit (%d MB) exceeds system available memory (%d MB)", 
                    memoryMB, systemMemoryMB));
            }

            // 将MB转换为字节
            Long memoryBytes = (long) memoryMB * 1024 * 1024;

            // 创建容器
            String containerId = metaService.createContainer(host, containerName, memoryBytes, image);
            return ResponseEntity.ok(containerId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to create container: " + e.getMessage());
        }
    }

    private boolean isValidImageName(String image) {
        return image != null && 
               image.matches("^[a-zA-Z0-9][a-zA-Z0-9_.-]*(/[a-zA-Z0-9][a-zA-Z0-9_.-]*)?(:[a-zA-Z0-9_.-]*)?$");
    }

}

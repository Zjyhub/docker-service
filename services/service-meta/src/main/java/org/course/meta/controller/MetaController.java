package org.course.meta.controller;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.course.container.ContainerVo;
import org.course.meta.model.ContainerStatVO;
import org.course.meta.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/lists")
    @Operation(summary = "获取容器列表", description = "获取所有机器docker中的所有容器")
    public List<ContainerVo> getServiceInstances() {
        return metaService.getContainerList();
    }


//    @GetMapping("/containers")
//    @Operation(summary = "获取容器列表",description = "获取当前机器docker中的所有容器")
//    public List<ContainerVo> getContainerList() {
//
//    }
//
    @GetMapping("/info")
    @Operation(summary = "获取容器信息",description = "根据容器id获取容器信息")
    public Container getContainerInfo(
            @RequestParam("id") String containerId,
            @RequestParam("host") String host
    ) {
       return metaService.getContainerInfo(containerId,host);
    }
//
    @PostMapping("/start")
    @Operation(summary = "启动容器",description = "根据容器id启动容器")
    public Boolean startContainer(
            @RequestParam("id") String containerId,
            @RequestParam("host") String host
    ) {
       return metaService.startContainer(containerId, host);
    }

    @PostMapping("/stop")
    @Operation(summary = "停止容器",description = "根据容器id停止容器")
    public Boolean stopContainer(
            @RequestParam("id") String containerId,
            @RequestParam("host") String host
    ) {
        return metaService.stopContainer(containerId, host);
    }

    @PostMapping("/remove")
    @Operation(summary = "删除容器",description = "根据容器id删除容器")
    public Boolean removeContainer(
            @RequestParam("id") String containerId,
            @RequestParam("host") String host
    ) {
        return metaService.removeContainer(containerId, host);
    }

    @PostMapping("restart")
    @Operation(summary = "重启容器",description = "根据容器id重启容器")
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

//    @GetMapping("/images")
//    @Operation(summary = "获取镜像列表",description = "获取当前机器docker中的所有镜像")
//    public List<Image> getImageList() {
//
//    }
}

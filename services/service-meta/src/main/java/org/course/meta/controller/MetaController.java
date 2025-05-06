package org.course.meta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.course.container.ContainerVo;
import org.course.meta.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
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
//    @GetMapping("/info")
//    @Operation(summary = "获取容器信息",description = "根据容器id获取容器信息")
//    public Container getContainerInfo(
//            @RequestParam("id") String containerId
//    ) {
//
//    }
//
//    @PostMapping("/start")
//    @Operation(summary = "启动容器",description = "根据容器id启动容器")
//    public Boolean startContainer(
//            @RequestParam("id") String containerId
//    ) {
//
//    }
//
//    @PostMapping("/stop")
//    @Operation(summary = "停止容器",description = "根据容器id停止容器")
//    public Boolean stopContainer(
//            @RequestParam("id") String containerId
//    ) {
//
//    }
//
//    @PostMapping("/remove")
//    @Operation(summary = "删除容器",description = "根据容器id删除容器")
//    public Boolean removeContainer(
//            @RequestParam("id") String containerId
//    ) {
//
//    }
//
//    @PostMapping("restart")
//    @Operation(summary = "重启容器",description = "根据容器id重启容器")
//    public Boolean restartContainer(
//            @RequestParam("id") String containerId
//    ) {
//
//    }
//
//
//    @GetMapping("/images")
//    @Operation(summary = "获取镜像列表",description = "获取当前机器docker中的所有镜像")
//    public List<Image> getImageList() {
//
//    }
}

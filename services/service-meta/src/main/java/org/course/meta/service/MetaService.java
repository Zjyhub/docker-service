package org.course.meta.service;

import com.github.dockerjava.api.model.Container;
import org.course.container.ContainerVo;

import java.util.List;

/**
 * Description:
 *
 * @author zjy
 */
public interface MetaService {
    List<ContainerVo> getContainerList();

    Container getContainerInfo(String id,String host);
}

package org.course.meta.service;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @author zjy
 */
public interface MetaService {
    List<ServiceInstance> getServiceInstances();
}

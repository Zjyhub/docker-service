package org.course.meta.service.impl;

import org.course.meta.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @author zjy
 */
@Service
public class MetaServiceImpl implements MetaService {

    private final DiscoveryClient discoveryClient;

    @Autowired
    public MetaServiceImpl (DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public List<ServiceInstance> getServiceInstances(){
        return discoveryClient.getInstances("service-docker");
    }

}

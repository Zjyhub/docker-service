package org.course.meta.service.impl;

import com.github.dockerjava.api.model.Container;
import org.course.container.ContainerVo;
import org.course.meta.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Description:
 *
 * @author zjy
 */
@Service
public class MetaServiceImpl implements MetaService {

    private final DiscoveryClient discoveryClient;

    private final RestTemplate restTemplate;

    @Autowired
    public MetaServiceImpl(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ContainerVo> getContainerList() {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        List<ContainerVo> containerVoList = new LinkedList<>();
        for (ServiceInstance instance : instances) {
            String url = instance.getUri().toString() + "/docker/containers";

            ContainerVo[] obj = restTemplate.getForObject(url, ContainerVo[].class);
            if (obj != null) {
                for (ContainerVo container : obj) {
                    container.setHost(instance.getHost());
                }
                containerVoList.addAll(Arrays.asList(obj));
            }
        }
        return containerVoList;
    }

    @Override
    public Container getContainerInfo(String id, String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/info?id=" + id;
                Container obj = restTemplate.getForObject(url, Container.class);
                return obj;
            }
        }
        return null;
    }
}

package org.course.meta.service.impl;

import org.course.container.DockerInfo;
import org.course.container.DockerStatus;
import org.course.meta.model.ContainerStatVO;
import org.course.meta.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    public List<ServiceInstance> getServiceInstances() {
        return discoveryClient.getInstances("service-docker");
    }

    @Override
    public DockerInfo getDockerInfo(String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/sysInfo";
                return restTemplate.getForObject(url, DockerInfo.class);
            }
        }
        return new DockerInfo();
    }

    @Override
    public DockerStatus getDockerStatus(String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/status";
                return restTemplate.getForObject(url, DockerStatus.class);
            }
        }
        return new DockerStatus();
    }


    @Override
    public Boolean startContainer(String id, String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/start?id=" + id;
                return restTemplate.postForObject(url, null, Boolean.class);
            }
        }
        return false;
    }

    @Override
    public Boolean stopContainer(String id, String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/stop?id=" + id;
                return restTemplate.postForObject(url, null, Boolean.class);
            }
        }
        return false;
    }

    @Override
    public Boolean removeContainer(String id, String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/remove?id=" + id;
                return restTemplate.postForObject(url, null, Boolean.class);
            }
        }
        return false;
    }

    @Override
    public Boolean restartContainer(String id, String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/restart?id=" + id;
                return restTemplate.postForObject(url, null, Boolean.class);
            }
        }
        return false;
    }

    @Override
    public List<ContainerStatVO> checkContainersStatus(double cpuThreshold, double memoryThreshold) {
        return new LinkedList<>();
    }


}

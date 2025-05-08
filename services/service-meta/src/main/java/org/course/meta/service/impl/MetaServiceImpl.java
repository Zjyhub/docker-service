package org.course.meta.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.course.container.ContainerStatus;
import org.course.container.DockerInfo;
import org.course.container.DockerStatus;
import org.course.meta.model.ContainerStatVO;
import org.course.meta.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author zjy
 */
@Slf4j
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
    public List<String> getServiceInstances() {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        List<String> hosts = new ArrayList<>();
        for (ServiceInstance instance : instances) {
            hosts.add(instance.getHost());
        }
        return hosts;
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
        List<ContainerStatVO> result = new ArrayList<>();
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");

        for (ServiceInstance instance : instances) {
            try {
                String url = instance.getUri().toString() + "/docker/status";
                DockerStatus dockerStatus = restTemplate.getForObject(url, DockerStatus.class);

                if (dockerStatus != null && dockerStatus.getContainers() != null) {
//                    for (ContainerStatus container : dockerStatus.getContainers()) {
//                        if (isContainerAbnormal(container, cpuThreshold, memoryThreshold)) {
//                            ContainerStatVO statVO = convertToContainerStatVO(container, instance.getHost(),
//                                    cpuThreshold, memoryThreshold);
//                            result.add(statVO);
//                        }
//                    }
                }
            } catch (Exception e) {
                log.error("Error checking containers for instance {}: {}",
                        instance.getHost(), e.getMessage());
            }
        }

        return result;
    }

    private boolean isContainerAbnormal(ContainerStatus container, double cpuThreshold, double memoryThreshold) {
        // 检查CPU使用率
        if (container.getCpuUsagePercent() > cpuThreshold) {
            return true;
        }

        // 检查内存使用率
        if (container.getMemLimit() > 0) {
            double memoryUsagePercent = (double) container.getMemUsage() / container.getMemLimit() * 100;
            return memoryUsagePercent > memoryThreshold;
        }

        return false;
    }

    private ContainerStatVO convertToContainerStatVO(ContainerStatus container, String host,
                                                     double cpuThreshold, double memoryThreshold) {
        ContainerStatVO statVO = new ContainerStatVO();
//        statVO.setContainerId(container.getContainerId());
        statVO.setHost(host);
        statVO.setCpuUsagePercentage(container.getCpuUsagePercent());

        // 计算内存使用率
        if (container.getMemLimit() > 0) {
            double memoryUsagePercent = (double) container.getMemUsage() / container.getMemLimit() * 100;
            statVO.setMemoryUsagePercentage(Math.round(memoryUsagePercent * 100.0) / 100.0);
        }

        // 设置状态
        statVO.setStatus(determineStatus(container.getCpuUsagePercent(),
                statVO.getMemoryUsagePercentage(), cpuThreshold, memoryThreshold));

        return statVO;
    }

    private String determineStatus(double cpuUsage, Double memoryUsage,
                                   double cpuThreshold, double memoryThreshold) {
        if (cpuUsage >= 90 || (memoryUsage != null && memoryUsage >= 90)) {
            return "CRITICAL";
        } else if (cpuUsage >= cpuThreshold || (memoryUsage != null && memoryUsage >= memoryThreshold)) {
            return "WARNING";
        }
        return "OK";
    }

    @Override
    public String createContainer(String host, String containerName, Long memoryBytes, String image) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/create";
                
                // 构建请求参数
                String requestUrl = String.format("%s?name=%s&memory=%d&image=%s",
                    url,
                    containerName,
                    memoryBytes,
                    image);
                
                try {
                    String containerId = restTemplate.postForObject(requestUrl, null, String.class);
                    if (containerId == null) {
                        throw new RuntimeException("Failed to create container: No container ID returned");
                    }
                    return containerId;
                } catch (Exception e) {
                    log.error("Failed to create container on host {}: {}", host, e.getMessage());
                    throw new RuntimeException("Failed to create container: " + e.getMessage());
                }
            }
        }
        throw new RuntimeException("No Docker service found for host: " + host);
    }
}

package cn.poile.ucs.resources;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 资源服务
 * @author: yaohw
 * @create: 2019-10-08 10:02
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class ResourceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServerApplication.class,args);
    }
}

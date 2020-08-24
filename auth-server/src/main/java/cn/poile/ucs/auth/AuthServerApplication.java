package cn.poile.ucs.auth;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;



/**
 * 认证中心服务
 * @author: yaohw
 * @create: 2019-09-25 16:48
 **/
@SpringBootApplication
@EnableDiscoveryClient
@ServletComponentScan
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class,args);
    }




}

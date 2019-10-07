package cn.poile.ucs.eureka.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Security 配置
 * @author: yaohw
 * @create: 2019-09-25 17:27
 **/
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 默认是开启，所以要关闭，否则其他服务无法注册到注册中心
        http.csrf().disable();
        super.configure(http);
    }
}

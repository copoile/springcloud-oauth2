package cn.poile.ucs.auth.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import java.util.List;

/**
 * @author: yaohw
 * @create: 2019-10-08 10:04
 **/
@Configuration
@EnableResourceServer
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "auth-server";

    @Autowired
    private FilterIgnorePropertiesConfig filterIgnorePropertiesConfig;


    @Override
    public void configure(HttpSecurity http) throws Exception {
        List<String> urls = filterIgnorePropertiesConfig.getUrls();
        int size = urls.size();
        http.authorizeRequests()
                .antMatchers(urls.toArray(new String[size])).permitAll()
                .anyRequest().authenticated();
    }


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID).stateless(true);
    }


}

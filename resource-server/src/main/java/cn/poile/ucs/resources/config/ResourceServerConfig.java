package cn.poile.ucs.resources.config;

import cn.poile.ucs.resources.constant.ErrorEnum;
import cn.poile.ucs.resources.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
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
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * @author: yaohw
 * @create: 2019-10-08 10:04
 **/
@Configuration
@EnableResourceServer
@EnableWebSecurity
// 启用方法级权限控制
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "resource-server";

    @Autowired
    private UserInfoTokenServices userInfoTokenServices;

    @Autowired
    private ResourceServerProperties sso;

    @Autowired
    private UserInfoRestTemplateFactory restTemplateFactory;


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 配置不需要安全拦截url
                .antMatchers("/test/no_need_token").permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated();

    }

    /**
     * 这个是跟服务绑定的，注意要跟client配置一致，如果客户端没有，则不能访问
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID);
        userInfoTokenServices.setPrincipalExtractor(principalExtractor());
        // 配置了user-info-uri默认使用的就是userInfoTokenServices，这个这么配置只是为了设置principalExtractor
        resources.tokenServices(tokenService());
        // token及权限校验不通过
        resources.authenticationEntryPoint(this.tokenErrorHandle())
                .accessDeniedHandler(this.deniedErrorHandle());
    }


    private ResourceServerTokenServices tokenService() {
        return new CustomUserInfoTokenServices(sso.getUserInfoUri(), restTemplateFactory.getUserInfoRestTemplate());
    }

    /**
     * 自定义Principal提取器，返回的Principal是一个map
     *
     * @return
     */
    @Bean
    public PrincipalExtractor principalExtractor() {
        return new CustomizePrincipalExtractor();
    }

    /**
     * 权限不足处理
     *
     * @return
     */
    private AccessDeniedHandler deniedErrorHandle() {
        return (request, response, accessDeniedException) -> {
            String json = handleResponse(ErrorEnum.ACCESS_DENIED.getErrorCode(), ErrorEnum.ACCESS_DENIED.getErrorMsg());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().print(json);
        };
    }

    /**
     * token验证失败处理
     *
     * @return
     */
    private AuthenticationEntryPoint tokenErrorHandle() {
        return (request, response, authException) -> {
            String message = authException.getMessage();
            String json;
            // client中的resourceIds不包含当前服务的resourceId也会走这里
            if (message != null && message.contains("resource id")) {
                json = handleResponse(ErrorEnum.ACCESS_DENIED.getErrorCode(), ErrorEnum.ACCESS_DENIED.getErrorMsg());
            } else {
                json = handleResponse(ErrorEnum.CREDENTIALS_INVALID.getErrorCode(), ErrorEnum.CREDENTIALS_INVALID.getErrorMsg());
            }
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().print(json);
        };
    }

    private String handleResponse(int code, String message) throws JsonProcessingException {
        ApiResponse body = new ApiResponse();
        body.setCode(code);
        body.setMessage(message);
        return new ObjectMapper().writeValueAsString(body);
    }

}

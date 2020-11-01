package cn.poile.ucs.auth.config;

import cn.poile.ucs.auth.constant.ErrorEnum;
import cn.poile.ucs.auth.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * 资源服务配置
 * @author: yaohw
 * @create: 2019-10-08 10:04
 **/
@Configuration
// 启用资源服务
@EnableResourceServer
// 启用方法级权限控制
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "auth-server";


    /**
     *  配置资源接口安全，http.authorizeRequests()针对的所有url，但是由于登录页面url包含在其中，这里配置会进行token校验，校验不通过返回错误json，
     *  而授权码模式获取code时需要重定向登录页面，重定向过程并不能携带token，所有不能用http.authorizeRequests()，
     *  而是用requestMatchers().antMatchers("")，这里配置的是需要资源接口拦截的url数组
     * @param http
     * @return void
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http    // 配置需要保护的资源接口
                .requestMatchers().antMatchers("/user", "/test/need_token", "/test/need_client_test", "/logout", "/remove", "/update", "/test/need_admin", "/test/scope")
                .and().authorizeRequests().anyRequest().authenticated();
    }

    /**
     * 这个是跟服务绑定的，注意要跟client配置一致，如果客户端没有，则不能访问
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.authenticationEntryPoint(this.tokenErrorHandle())
                .accessDeniedHandler(this.deniedErrorHandle());
        resources.resourceId(RESOURCE_ID).stateless(true);
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

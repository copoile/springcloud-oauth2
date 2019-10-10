package cn.poile.ucs.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

/**
 * redis授权码模式授权码服务-操作授权码生成、存储、删除
 * @author: yaohw
 * @create: 2019-10-10 18:21
 **/
@Configuration
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

    private static final String AUTHORIZATION_CODE = "authorization:code:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     *  将随机生成的授权码存到redis中
     * @param code
     * @param authentication
     * @return void
     */
    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        redisTemplate.opsForValue().set(AUTHORIZATION_CODE+code,authentication);
    }

    /**
     *  取出授权码并删除授权码
     * @param code
     * @return org.springframework.security.oauth2.provider.OAuth2Authentication
     */
    @Override
    protected OAuth2Authentication remove(String code) {
        Object o = redisTemplate.opsForValue().get(AUTHORIZATION_CODE + code);
        if (o != null) {
            redisTemplate.delete(AUTHORIZATION_CODE+code);
        }
        return (OAuth2Authentication) o;
    }
}

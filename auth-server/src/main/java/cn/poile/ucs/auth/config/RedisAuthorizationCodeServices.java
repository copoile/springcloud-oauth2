package cn.poile.ucs.auth.config;
/**
 * 注意一点，这里存的redis的序列表用默认的JdkSerializationStrategy，跟RedisTokenStore类似
 * 不能用json的，使用json时反序列成token的时候会报错，非要用json的需要同时修改token序列化方式
 */

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;

/**
 * redis授权码模式授权码服务-操作授权码生成、存储、删除
 *
 * @author: yaohw
 * @create: 2019-10-10 18:21
 **/
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {


    private static final String AUTHORIZATION_CODE = "authorization:code:";

    /**
     * 授权码有效时长
     */
    private long expiration = 300L;

    /**
     * key 前缀
     */
    private String prefix = "";


    private final RedisConnectionFactory connectionFactory;
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();


    public RedisAuthorizationCodeServices(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;

    }


    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }


    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }

    /**
     * value序列化
     * @param object
     * @return
     */
    private byte[] serialize(Object object) {
        return serializationStrategy.serialize(object);
    }

    /**
     * key序列化
     * @param string
     * @return
     */
    private byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }

    /**
     * key序列化
     * @param object
     * @return
     */
    private byte[] serializeKey(Object object) {
        return serialize(prefix + object);
    }


    /**
     * 反序列化
     * @param bytes
     * @return
     */
    private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2Authentication.class);
    }





    /**
     * 将随机生成的授权码存到redis中
     *
     * @param code
     * @param authentication
     * @return void
     */
    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        byte[] serializedKey = serializeKey(AUTHORIZATION_CODE + code);
        byte[] serializedAuthentication = serialize(authentication);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            conn.set(serializedKey, serializedAuthentication);
            conn.expire(serializedKey,expiration);
            conn.closePipeline();
        } finally {
            conn.close();
        }

    }

    /**
     * 取出授权码并删除授权码(权限码只能用一次，调试时可不删除，code就可多次使用)
     *
     * @param code
     * @return org.springframework.security.oauth2.provider.OAuth2Authentication
     */
    @Override
    protected OAuth2Authentication remove(String code) {
        byte[] serializedKey = serializeKey(AUTHORIZATION_CODE + code);
        RedisConnection conn = getConnection();
        byte[] bytes;
        try {
            bytes = conn.get(serializedKey);
            if (bytes != null) {
                conn.del(serializedKey);
            }
        } finally {
            conn.close();
        }
        return deserializeAuthentication(bytes);
    }


}

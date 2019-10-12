package cn.poile.ucs.resources.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Map;

/**
 * 自定义principal提取器
 * @author: yaohw
 * @create: 2019-10-09 12:01
 **/
public class CustomizePrincipalExtractor implements PrincipalExtractor {

    /**
     * Extract the principal that should be used for the token.
     *
     * @param map the source map
     * @return the extracted principal or {@code null}
     */
    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        //这直接返回map本身，该map包含的认证中心对的principal的所有字段（key为字段名，value为字段值形式）
        //这里也可以new一个user对象，将map对应字段值映射到user对象中返回user对象
        return map;
    }
}

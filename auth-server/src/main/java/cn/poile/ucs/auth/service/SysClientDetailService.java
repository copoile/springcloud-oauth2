package cn.poile.ucs.auth.service;

import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *  Oauth客户端服务
 * @author: yaohw
 * @create: 2019-10-12 17:33
 **/
@Service
public class SysClientDetailService {

    /**
     * 根据客户端id查询
     * @param clientId
     * @return org.springframework.security.oauth2.provider.client.BaseClientDetails
     */
    public BaseClientDetails selectById(String clientId) {
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setAuthorities(new ArrayList<>());
        clientDetails.setClientId("yaohw");
        // 这个客户端秘钥和密码一样存BCryptPasswordEncoder加密后的接口，具体看定义的加密器
        clientDetails.setClientSecret("$2a$10$CwIutywnbs9bifHaY3Ezu.gYkWi4Zano8gVPq08hXjal6.uj.Yzuy");
        // 设置accessToken和refreshToken的时效，如果不设置则使tokenServices的配置的
        clientDetails.setAccessTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(2));
        clientDetails.setRefreshTokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(30));
        // 资源id列表，需要注意的是这里配置的需要与ResourceServerConfig中配置的相匹配
        List<String> resourceIds = new ArrayList<>();
        resourceIds.add("auth-server");
        resourceIds.add("resource-server");
        clientDetails.setResourceIds(resourceIds);
        List<String> scopes = new ArrayList<>(1);
        scopes.add("sever");
        clientDetails.setScope(scopes);
        List<String> grantTypes = new ArrayList<>(5);
        grantTypes.add("password");
        grantTypes.add("refresh_token");
        grantTypes.add("authorization_code");
        grantTypes.add("implicit");
        grantTypes.add("mobile");
        clientDetails.setAuthorizedGrantTypes(grantTypes);
        Set<String> sets = new HashSet<>(1);
        sets.add("http://www.baidu.com");
        clientDetails.setRegisteredRedirectUri(sets);
        List<String> autoApproveScopes = new ArrayList<>(1);
        autoApproveScopes.add("sever");
        // 自动批准作用于，授权码模式时使用，登录验证后直接返回code，不再需要下一步点击授权
        clientDetails.setAutoApproveScopes(autoApproveScopes);
        return clientDetails;
    }
}

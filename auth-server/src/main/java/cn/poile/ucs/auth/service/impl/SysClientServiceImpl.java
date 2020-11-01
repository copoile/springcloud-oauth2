package cn.poile.ucs.auth.service.impl;

import cn.poile.ucs.auth.entity.SysAuthority;
import cn.poile.ucs.auth.entity.SysClient;
import cn.poile.ucs.auth.mapper.SysClientMapper;
import cn.poile.ucs.auth.service.ISysAuthorityService;
import cn.poile.ucs.auth.service.ISysClientService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2020-09-07
 */
@Service
public class SysClientServiceImpl extends ServiceImpl<SysClientMapper, SysClient> implements ISysClientService, ClientDetailsService {

    @Autowired
    private ISysAuthorityService sysAuthorityService;

    private static final String SEPARATOR = ",";

    /**
     * Load a client by the client id. This method must not return null.
     *
     * @param clientId The client id.
     * @return The client details (never null).
     * @throws ClientRegistrationException If the client account is locked, expired, disabled, or invalid for any other reason.
     */
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        LambdaQueryWrapper<SysClient> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClient::getClientId, clientId);
        SysClient sysClient = getOne(queryWrapper, false);
        if (sysClient == null) {
            throw new ClientRegistrationException("client not found");
        }
        return this.buildBaseClientDetails(sysClient);
    }

    /**
     * 构建ClientDetails
     *
     * @param sysClient 系统客户端
     * @return ClientDetails
     */
    private ClientDetails buildBaseClientDetails(SysClient sysClient) {
        BaseClientDetails baseClientDetails = new BaseClientDetails();
        baseClientDetails.setClientId(sysClient.getClientId());
        baseClientDetails.setAutoApproveScopes(new ArrayList<>());
        String autoApproveScopes = sysClient.getAutoApproveScopes();
        if (StringUtils.isNotBlank(autoApproveScopes)) {
            String[] split = StringUtils.split(autoApproveScopes, SEPARATOR);
            baseClientDetails.setAutoApproveScopes(Arrays.asList(split));
        }
        baseClientDetails.setClientSecret(sysClient.getClientSecret());
        baseClientDetails.setScope(new ArrayList<>());
        String scopes = sysClient.getScopes();
        if (StringUtils.isNotBlank(scopes)) {
            String[] split = StringUtils.split(scopes, SEPARATOR);
            baseClientDetails.setScope(Arrays.asList(split));
        }
        String resourceIds = sysClient.getResourceIds();
        baseClientDetails.setResourceIds(new ArrayList<>());
        if (StringUtils.isNotBlank(resourceIds)) {
            String[] split = StringUtils.split(resourceIds, SEPARATOR);
            baseClientDetails.setResourceIds(Arrays.asList(split));
        }
        String grantTypes = sysClient.getGrantTypes();
        if (StringUtils.isNotBlank(grantTypes)) {
            String[] split = StringUtils.split(grantTypes, SEPARATOR);
            baseClientDetails.setAuthorizedGrantTypes(Arrays.asList(split));
        }
        String redirectUris = sysClient.getRedirectUris();
        baseClientDetails.setRegisteredRedirectUri(Sets.newHashSet());
        if (StringUtils.isNotBlank(redirectUris)) {
            String[] split = StringUtils.split(redirectUris, SEPARATOR);
            baseClientDetails.setRegisteredRedirectUri(Sets.newHashSet(split));
        }
        String authorityIds = sysClient.getAuthorityIds();
        baseClientDetails.setAuthorities(new ArrayList<>());
        if (StringUtils.isNotBlank(authorityIds)) {
            String[] split = StringUtils.split(authorityIds, SEPARATOR);
            LambdaQueryWrapper<SysAuthority> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysAuthority::getId, Arrays.asList(split));
            List<SysAuthority> sysAuthorityList = sysAuthorityService.list(queryWrapper);
            baseClientDetails.setAuthorities(sysAuthorityList);
        }
        baseClientDetails.setAccessTokenValiditySeconds(Math.toIntExact(sysClient.getAccessTokenValiditySeconds()));
        baseClientDetails.setRefreshTokenValiditySeconds(Math.toIntExact(sysClient.getRefreshTokenValiditySeconds()));
        // 附加信息
        // baseClientDetails.setAdditionalInformation();
        return baseClientDetails;
    }


}

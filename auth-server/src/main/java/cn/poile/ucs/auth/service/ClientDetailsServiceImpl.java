package cn.poile.ucs.auth.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

/**
 * @author: yaohw
 * @create: 2019-10-12 16:12
 **/
@Service
@Log4j2
public class ClientDetailsServiceImpl implements ClientDetailsService {

    @Autowired
    private SysClientDetailService clientDetailService;

    /**
     * Load a client by the client id. This method must not return null.
     *
     * @param clientId The client id.
     * @return The client details (never null).
     * @throws ClientRegistrationException If the client account is locked, expired, disabled, or invalid for any other reason.
     */
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        log.info("客户端查询:" + clientId);
        BaseClientDetails baseClientDetails = clientDetailService.selectById(clientId);
        if (baseClientDetails == null) {
            throw new NoSuchClientException("not found clientId:" + clientId);
        }
        return baseClientDetails;
    }
}

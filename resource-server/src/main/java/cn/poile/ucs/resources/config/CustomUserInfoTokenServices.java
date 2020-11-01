package cn.poile.ucs.resources.config;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedAuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import java.util.*;

/**
 * 自定义获取用户认证信息
 *
 * @author: yaohw
 * @create: 2020/11/1 3:07 下午
 */
@Log4j2
public class CustomUserInfoTokenServices implements ResourceServerTokenServices {

    private final String userInfoEndpointUrl;

    private final OAuth2RestOperations restTemplate;

    private static final String API_CODE = "code";

    private static final int SUCCESS_CODE = 0;

    private static final String API_DATA = "data";

    private static final String OAUTH2_REQUEST = "oauth2Request";

    private static final String RESOURCE_IDS = "resourceIds";


    private PrincipalExtractor principalExtractor = new CustomizePrincipalExtractor();

    private AuthoritiesExtractor authoritiesExtractor = new FixedAuthoritiesExtractor();

    public CustomUserInfoTokenServices(String userInfoEndpointUrl, OAuth2RestOperations restTemplate) {
        this.userInfoEndpointUrl = userInfoEndpointUrl;
        this.restTemplate = restTemplate;
    }

    /**
     * Load the credentials for the specified access token.
     *
     * @param accessToken The access token value.
     * @return The authentication for the access token.
     * @throws AuthenticationException If the access token is expired
     * @throws InvalidTokenException   if the token isn't valid
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        Map<String, Object> map = getMap(this.userInfoEndpointUrl, accessToken);
        if (!map.containsKey(API_CODE) || !map.get(API_CODE).equals(SUCCESS_CODE)) {
            throw new InvalidTokenException(accessToken);
        }
        return extractAuthentication((Map<String, Object>) map.get(API_DATA));
    }

    private OAuth2Authentication extractAuthentication(Map<String, Object> map) {
        Object principal = getPrincipal(map);
        List<GrantedAuthority> authorities = this.authoritiesExtractor
                .extractAuthorities(map);
        Set<String> resourceIds = extractResourceIds(map);
        OAuth2Request request = new OAuth2Request(null, null, null, true, null,
                resourceIds, null, null, null);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                principal, "N/A", authorities);
        token.setDetails(map);
        return new OAuth2Authentication(request, token);
    }

    @SuppressWarnings({"unchecked"})
    private Set<String> extractResourceIds(Map<String, Object> map) {
        if (!map.containsKey(OAUTH2_REQUEST)) {
            return null;
        }
        Map<String, Object> oauth2Request = (Map<String, Object>) map.get(OAUTH2_REQUEST);
        if (oauth2Request == null) {
            return null;
        }
        Object resourceIds = oauth2Request.get(RESOURCE_IDS);
        if (resourceIds == null) {
            return null;
        }
        String strArr = resourceIds.toString();
        String[] split = StringUtils.split(strArr.replace("[", "").replace("]", ""), ",");
        return new HashSet<>(Arrays.asList(split));
    }

    /**
     * Return the principal that should be used for the token. The default implementation
     * delegates to the {@link PrincipalExtractor}.
     *
     * @param map the source map
     * @return the principal or {@literal "unknown"}
     */
    private Object getPrincipal(Map<String, Object> map) {
        Object principal = this.principalExtractor.extractPrincipal(map);
        return (principal == null ? "unknown" : principal);
    }

    /**
     * Retrieve the full access token details from just the value.
     *
     * @param accessToken the token value
     * @return the full access token with client id etc.
     */
    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        return null;
    }

    @SuppressWarnings({"unchecked"})
    private Map<String, Object> getMap(String path, String accessToken) {
        try {
            OAuth2RestOperations restTemplate = this.restTemplate;
            if (restTemplate == null) {
                BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
                restTemplate = new OAuth2RestTemplate(resource);
            }
            OAuth2AccessToken existingToken = restTemplate.getOAuth2ClientContext()
                    .getAccessToken();
            if (existingToken == null || !accessToken.equals(existingToken.getValue())) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(
                        accessToken);
                String tokenType = DefaultOAuth2AccessToken.BEARER_TYPE;
                token.setTokenType(tokenType);
                restTemplate.getOAuth2ClientContext().setAccessToken(token);
            }
            return restTemplate.getForEntity(path, Map.class).getBody();
        } catch (Exception ex) {
            log.warn("Could not fetch user details: " + ex.getClass() + ", "
                    + ex.getMessage());
            return Collections.<String, Object>singletonMap("error",
                    "Could not fetch user details");
        }
    }
}

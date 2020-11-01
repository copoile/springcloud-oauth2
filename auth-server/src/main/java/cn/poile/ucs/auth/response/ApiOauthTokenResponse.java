package cn.poile.ucs.auth.response;

import cn.poile.ucs.auth.common.CustomOauthToken;
import cn.poile.ucs.auth.constant.ErrorEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author: yaohw
 * @create: 2020/10/30 11:17 下午
 */
@JsonSerialize
@JsonDeserialize
public class ApiOauthTokenResponse implements Serializable, OAuth2AccessToken {

    @Setter
    @Getter
    private int code;

    @Setter
    @Getter
    private String message;

    @Getter
    @Setter
    private CustomOauthToken data;

    @Setter
    @JsonIgnore
    private OAuth2RefreshToken refreshToken;

    public ApiOauthTokenResponse(OAuth2AccessToken accessToken) {
        setCode(ErrorEnum.SUCCESS.getErrorCode());
        setMessage(ErrorEnum.SUCCESS.getErrorMsg());

        CustomOauthToken data = new CustomOauthToken();
        data.setToken(accessToken.getValue());
        data.setExpiration(accessToken.getExpiration());
        data.setTokenType(accessToken.getTokenType());
        OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
        data.setRefreshToken(refreshToken.getValue());
        setRefreshToken(refreshToken);
        data.setScope(accessToken.getScope());
        Map<String, Object> additionalInformation = accessToken.getAdditionalInformation();
        if (additionalInformation == null || additionalInformation.isEmpty()) {
            additionalInformation = null;
        }
        data.setAdditionalInformation(additionalInformation);
        setData(data);
    }

    /**
     * 注意这个不能返回null，要不然会报空指针异常
     */
    @JsonIgnore
    @Override
    public Map<String, Object> getAdditionalInformation() {
        return data.getAdditionalInformation() == null ? new HashMap<>() : data.getAdditionalInformation();
    }

    @JsonIgnore
    @Override
    public Set<String> getScope() {
        return data.getScope();
    }

    @JsonIgnore
    @Override
    public OAuth2RefreshToken getRefreshToken() {
        return this.refreshToken;
    }

    @JsonIgnore
    @Override
    public String getTokenType() {
        return data.getTokenType();
    }

    @JsonIgnore
    @Override
    public boolean isExpired() {
        Date expiration = this.getExpiration();
        return expiration != null && expiration.before(new Date());
    }

    @JsonIgnore
    @Override
    public Date getExpiration() {
        return data.getExpiration();
    }

    @JsonIgnore
    @Override
    public int getExpiresIn() {
        Date expiration = this.getExpiration();
        return expiration != null ? Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
                .intValue() : 0;
    }

    @JsonIgnore
    @Override
    public String getValue() {
        return data.getToken();
    }
}

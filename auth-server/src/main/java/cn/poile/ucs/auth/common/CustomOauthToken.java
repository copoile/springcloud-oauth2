package cn.poile.ucs.auth.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 定制化返回token
 *
 * @author: yaohw
 * @create: 2020/10/30 11:09 下午
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomOauthToken implements Serializable {

    public static String BEARER_TYPE = "Bearer";

    private String token;

    @JsonIgnore
    private Date expiration;

    private String tokenType = BEARER_TYPE.toLowerCase();

    private String refreshToken;

    private Set<String> scope;

    private Map<String, Object> additionalInformation;

    @JsonProperty("expire")
    public int getExpiresIn() {
        Date expiration = this.expiration;
        return expiration != null ? Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
                .intValue() : 0;
    }

}

package cn.poile.ucs.auth.exception;

import cn.poile.ucs.auth.common.CustomOauthTokenExceptionJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @author: yaohw
 * @create: 2020/10/31 11:14 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonSerialize(using = CustomOauthTokenExceptionJsonSerializer.class)
public class CustomOauthTokenException extends OAuth2Exception {


    private int code;

    private String message;

    public CustomOauthTokenException(String msg, Throwable t) {
        super(msg, t);
    }

    public CustomOauthTokenException(String msg) {
        super(msg);
    }

    public CustomOauthTokenException(int code, String message) {
        super(message);
        setCode(code);
        setMessage(message);
    }
}

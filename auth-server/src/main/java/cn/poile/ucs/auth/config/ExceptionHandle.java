package cn.poile.ucs.auth.config;

import cn.poile.ucs.auth.exception.ApiException;
import cn.poile.ucs.auth.exception.CustomOauthTokenException;
import cn.poile.ucs.auth.response.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static cn.poile.ucs.auth.constant.ErrorEnum.*;

/**
 * 统一异常处理
 * RestControllerAdvice 加扫包，要不然有个问题，扫到AuthorizationEndpoint的话，授权码模式和简化模式会有问题
 *
 * @author: yaohw
 * @create: 2020/10/25 12:13 下午
 */
@RestControllerAdvice(basePackages = "cn.poile.ucs.auth.controller")
@ResponseBody
@Log4j2
public class ExceptionHandle implements WebResponseExceptionTranslator<OAuth2Exception> {


    @ExceptionHandler(Exception.class)
    public ApiResponse handleException(Exception e) {
        log.error("未知异常:{0}", e);
        return new ApiResponse(SYSTEM_ERROR.getErrorCode(), SYSTEM_ERROR.getErrorMsg());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse handleAccessDeniedException(Exception e) {
        return new ApiResponse(ACCESS_DENIED.getErrorCode(), ACCESS_DENIED.getErrorMsg());
    }

    @ExceptionHandler(ApiException.class)
    public ApiResponse handleApiException(Exception e) {
        log.error("api异常:{0}", e);
        return new ApiResponse(SYSTEM_ERROR.getErrorCode(), SYSTEM_ERROR.getErrorMsg());
    }


    @ExceptionHandler(CustomOauthTokenException.class)
    public ApiResponse handleCustomOauthTokenException(CustomOauthTokenException e) {
        return new ApiResponse(e.getCode(), e.getMessage());
    }


    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) {
        CustomOauthTokenException body = this.handleOAuth2Exception(e);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    /**
     * 认证相关异常处理
     *
     * @param e e
     * @return CustomOauthTokenException
     */
    private CustomOauthTokenException handleOAuth2Exception(Exception e) {
        if (e instanceof BadCredentialsException) {
            return new CustomOauthTokenException(BAD_CREDENTIALS.getErrorCode(), BAD_CREDENTIALS.getErrorMsg());
        }
        if (e instanceof LockedException) {
            return new CustomOauthTokenException(ACCOUNT_DISABLE.getErrorCode(), ACCOUNT_DISABLE.getErrorMsg());
        }
        if (e instanceof AccountExpiredException) {
            return new CustomOauthTokenException(ACCOUNT_EXPIRED.getErrorCode(), ACCOUNT_EXPIRED.getErrorMsg());
        }
        if (e instanceof CredentialsExpiredException) {
            return new CustomOauthTokenException(CREDENTIALS_EXPIRED.getErrorCode(), CREDENTIALS_EXPIRED.getErrorMsg());
        }
        if (e instanceof InvalidGrantException) {
            // 刷新token认证时
            String message = e.getMessage();
            if (message != null && message.contains("Invalid refresh token")) {
                return new CustomOauthTokenException(REFRESH_CREDENTIALS_INVALID.getErrorCode(), REFRESH_CREDENTIALS_INVALID.getErrorMsg());
            }
            // 授权码认证时
            if (message != null && message.contains("Invalid authorization code")) {
                return new CustomOauthTokenException(INVALID_REQUEST.getErrorCode(), "授权码无效或已过期");
            }
            return new CustomOauthTokenException(BAD_CREDENTIALS.getErrorCode(), BAD_CREDENTIALS.getErrorMsg());
        }
        // 无效grant_type
        if (e instanceof UnsupportedGrantTypeException) {
            return new CustomOauthTokenException(INVALID_REQUEST.getErrorCode(), "无效认证类型");
        }
        // 自定义的认证模式可以直接用这个，可参考mobileCodeAuthenticationProvider
        if (e instanceof CustomOauthTokenException) {
            CustomOauthTokenException exception = (CustomOauthTokenException) e;
            return new CustomOauthTokenException(exception.getCode(), exception.getMessage());
        }
        // 范围不正确
        if (e instanceof InvalidScopeException) {

        }
        log.error("未知认证异常:{0}", e);
        return new CustomOauthTokenException(SYSTEM_ERROR.getErrorCode(), SYSTEM_ERROR.getErrorMsg());
    }
}

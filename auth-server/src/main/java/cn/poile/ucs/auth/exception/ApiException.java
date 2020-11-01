package cn.poile.ucs.auth.exception;


import cn.poile.ucs.auth.constant.ErrorEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: yaohw
 * @create: 2020/10/27 10:25 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApiException extends RuntimeException {

    private int code;

    private String message;

    public ApiException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiException(String message) {
        this.code = ErrorEnum.INVALID_REQUEST.getErrorCode();
        this.message = message;
    }
}

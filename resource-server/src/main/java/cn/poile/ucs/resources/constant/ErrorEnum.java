package cn.poile.ucs.resources.constant;


/**
 * @author: yaohw
 * @create: 2020/10/25 1:11 下午
 */
public enum ErrorEnum {
    /**
     * 成功
     */
    SUCCESS(0, "成功"),

    /**
     * 用户名或密码错误
     */
    BAD_CREDENTIALS(1001, "用户名或密码错误"),

    /**
     * 手机号或验证码不正确
     */
    BAD_MOBILE_CODE(1002, "手机号或验证码不正确"),

    /**
     * 账号禁用
     */
    ACCOUNT_DISABLE(1003, "账号已禁用"),

    /**
     * 账号已过期
     */
    ACCOUNT_EXPIRED(1004, "账号已过期"),

    /**
     * 账号已锁定
     */
    ACCOUNT_LOCKED(1005, "账号已锁定"),

    /**
     * 凭证已过期
     */
    CREDENTIALS_EXPIRED(1006, "凭证已过期"),

    /**
     * 不允许访问
     */
    ACCESS_DENIED(1007, "不允许访问"),

    /**
     * 无权限访问
     */
    PERMISSION_DENIED(1008, "无权限访问"),

    /**
     * 凭证无效或已过期
     */
    CREDENTIALS_INVALID(1009, "凭证无效或已过期"),

    /**
     * 刷新凭证无效或已过期
     */
    REFRESH_CREDENTIALS_INVALID(1010, "刷新凭证无效或已过期"),

    /**
     * 无效请求
     */
    INVALID_REQUEST(1011, "无效请求或请求不接受"),


    /**
     * 系统异常
     */
    SYSTEM_ERROR(5000, "系统异常");


    /**
     * 错误码
     */
    private Integer errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    ErrorEnum(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


    public String getErrorMsg() {
        return errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}

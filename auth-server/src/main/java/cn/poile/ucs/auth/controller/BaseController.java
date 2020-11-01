package cn.poile.ucs.auth.controller;

import cn.poile.ucs.auth.constant.ErrorEnum;
import cn.poile.ucs.auth.response.ApiResponse;

/**
 * @author: yaohw
 * @create: 2020/8/16 5:36 下午
 */
public class BaseController {

    private <T> ApiResponse<T> init() {
        return new ApiResponse<>();
    }

    protected <T> ApiResponse<T> createResponse() {
        ApiResponse<T> response = init();
        response.setCode(ErrorEnum.SUCCESS.getErrorCode());
        response.setMessage(ErrorEnum.SUCCESS.getErrorMsg());
        return response;
    }

    protected <T> ApiResponse<T> createResponse(T body) {
        ApiResponse<T> response = createResponse();
        response.setData(body);
        return response;
    }
}

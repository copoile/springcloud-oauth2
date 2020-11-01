package cn.poile.ucs.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * @author: yaohw
 * @create: 2020/10/25 12:14 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor()
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * 响应码
     */
    @NonNull
    private int code;

    /**
     * 响应信息
     */
    @NonNull
    private String message;

    /**
     * 响应数据
     */
    private T data;
}

package cn.poile.ucs.resources.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yaohw
 * @create: 2019-10-08 11:37
 **/
@RestController
@Log4j2
public class TestRestController {


    /**
     * 不需要token访问测试
     * @return
     */
    @GetMapping("/test/no_need_token")
    public @ResponseBody String test() {
        return "no_need_token";
    }

    /**
     * 需要需要token访问接口测试
     * @return
     */
    @GetMapping("/test/need_token")
    public @ResponseBody String test2(Authentication authentication) {
        log.info("{}",authentication);
        // 由于自定义的principal返回的是包含全部user字段的map
        Object principal = authentication.getPrincipal();
        return "need_token";
    }

    /**
     * 需要需要管理员权限
     * @return
     */
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/test/need_admin")
    public @ResponseBody String admin() {
        return "need_admin";
    }

    /**
     * 需要需要管理员权限
     *
     * @return
     */
    @PreAuthorize("hasAuthority('admin2')")
    @GetMapping("/test/need_admin2")
    public @ResponseBody
    String admin2() {
        return "need_admin2";
    }

    /**
     * 测试需要客户端的test权限
     *
     * @return
     */
    @PreAuthorize("#oauth2.clientHasRole('test')")
    @GetMapping("/test/need_client_test")
    public @ResponseBody
    String test4() {
        return "need_client_test";
    }

    /**
     * scope 控制测试,该方法只有配置有scope为sever2的客户端能访问，针对的是客户端
     *
     * @return
     */
    @GetMapping("/test/scope")
    @PreAuthorize("#oauth2.hasScope('sever2')")
    public @ResponseBody
    String test3() {
        return "scope-test";
    }

}

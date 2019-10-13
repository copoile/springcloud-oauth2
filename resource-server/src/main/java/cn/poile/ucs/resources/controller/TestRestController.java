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
        //由于自定义的principal返回的是包含全部user字段的map
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

}

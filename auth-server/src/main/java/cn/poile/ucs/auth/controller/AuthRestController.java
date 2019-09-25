package cn.poile.ucs.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yaohw
 * @create: 2019-09-25 16:49
 **/
@RestController
public class AuthRestController {

    @GetMapping("/test")
    public String test() {
    return "ok";
    }
}

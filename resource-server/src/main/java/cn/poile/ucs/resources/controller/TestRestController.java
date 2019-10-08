package cn.poile.ucs.resources.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yaohw
 * @create: 2019-10-08 11:37
 **/
@RestController
@Log4j2
public class TestRestController {

    @GetMapping("/t")
    public Object est(Authentication authentication) {
        log.info("{}",authentication);
        Object principal = authentication.getPrincipal();
        log.info("{}",principal);
        return principal;
    }
}

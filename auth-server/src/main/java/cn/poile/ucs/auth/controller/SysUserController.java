package cn.poile.ucs.auth.controller;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 系统用户表 前端控制器
 * </p>
 *
 * @author yaohw
 * @since 2020-09-07
 */
@RestController
@RequestMapping("/sysUser")
public class SysUserController extends BaseController {

    /**
     * 123456的密码密文
     */
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        // $2a$10$9ustgCuVxydWmgVVA7U9..vONYU8n8yrjJLkg5GsuBNCNHeGKbIOe
        System.out.printf(encode);
    }

}

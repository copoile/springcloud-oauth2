package cn.poile.ucs.auth.controller;

import cn.poile.ucs.auth.util.UserDetailImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

/**
 * @author: yaohw
 * @create: 2019-09-25 16:49
 **/
@Controller
@Log4j2
public class AuthRestController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 更新用户信息时更新redis中的用户信息
     * @param authentication
     * @return java.lang.String
     */
    @GetMapping("/update")
    public @ResponseBody String updateCacheUserInfo(Authentication authentication) {
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;
            Authentication userAuthentication = auth2Authentication.getUserAuthentication();
            OAuth2Authentication newOAuth2Authentication = null;
            if (userAuthentication instanceof UsernamePasswordAuthenticationToken) {
                UserDetailImpl userDetails = (UserDetailImpl)userDetailsService.loadUserByUsername("yaohw");
                userDetails.setUsername("yaohw2");
                userDetails.setTest("test333");
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
                newOAuth2Authentication= new OAuth2Authentication(auth2Authentication.getOAuth2Request(),usernamePasswordAuthenticationToken);
            }
            OAuth2AccessToken accessToken = tokenStore.getAccessToken(auth2Authentication);
            if (newOAuth2Authentication!=null) {
                tokenStore.storeAccessToken(accessToken,newOAuth2Authentication);
            }
        }
        return "ok";
    }
    @GetMapping("/user")
    public @ResponseBody Object userInfo(Principal user) {
        log.info("user:{}",user);
        return  user;
    }

    @DeleteMapping("/logOut")
    public @ResponseBody String logOut(@RequestHeader(value = "Authorization") String authorization) {
        String accessToken = authorization.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
        consumerTokenServices.revokeToken(accessToken);
        return "ok";
    }

    @GetMapping("/test")
    public @ResponseBody String test() {
        return "ok";
    }

    /**
     * 认证页面
     * @return ModelAndView
     */
    @GetMapping("/require")
    public ModelAndView require() {
        log.info("认证页面");
        return new ModelAndView("ftl/login");
    }

}

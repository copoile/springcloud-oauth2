package cn.poile.ucs.auth.controller;

import cn.poile.ucs.auth.Token.MobileCodeAuthenticationToken;
import cn.poile.ucs.auth.service.UserDetailsServiceImpl;
import cn.poile.ucs.auth.util.UserDetailImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yaohw
 * @create: 2019-09-25 16:49
 **/
@RestController
@Log4j2
public class AuthRestController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/update")
    public String test(Authentication authentication) {
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;
            OAuth2Request oAuth2Request = auth2Authentication.getOAuth2Request();
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
    public UserDetailImpl test2(Authentication authentication) {
        log.info("user:{}",authentication);
        return  (UserDetailImpl)authentication.getPrincipal();
    }

    @DeleteMapping("/logOut")
    public String logOut(@RequestHeader(value = "Authorization") String token) {
        String[] tokenArr = token.split(" ");
        if (tokenArr.length!=2) {
            return "error";
        }
        String accessToken = tokenArr[1];
        consumerTokenServices.revokeToken(accessToken);
        return "ok";
    }


}

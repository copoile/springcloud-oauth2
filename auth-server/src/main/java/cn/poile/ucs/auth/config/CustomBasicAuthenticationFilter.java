package cn.poile.ucs.auth.config;

import cn.poile.ucs.auth.constant.ErrorEnum;
import cn.poile.ucs.auth.response.ApiResponse;
import cn.poile.ucs.auth.service.impl.SysClientServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 这个过滤器主要是替代原生的BasicAuthenticationFilter，客户端信息异常时原生的不能返回统一json，故自定义
 *
 * @author: yaohw
 * @create: 2020/10/31 2:23 下午
 */
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomBasicAuthenticationFilter extends OncePerRequestFilter {

    private static final String BASIC = "basic ";


    private SysClientServiceImpl sysClientService;

    public PasswordEncoder passwordEncoder;


    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.sysClientService,
                "An sysClientService is required");
        Assert.notNull(this.passwordEncoder,
                "An passwordEncoder is required");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        logger.info("Basic Authentication");
        String header = request.getHeader("Authorization");
        if (header == null || !header.toLowerCase().startsWith(BASIC)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            String[] tokens = extractAndDecodeHeader(header, request);
            assert tokens.length == 2;

            String username = tokens[0];
            String password = tokens[1];

            ClientDetails clientDetails = sysClientService.loadClientByClientId(username);
            if (clientDetails == null || !passwordEncoder.matches(password, clientDetails.getClientSecret())) {
                throw new IllegalArgumentException("invalid client");
            }
            chain.doFilter(request, response);
            return;
        } catch (Exception e) {
            // create json response
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().print(createErrorResponse());
        }
    }

    /**
     * 自定义返回统一json格式
     *
     * @return
     * @throws JsonProcessingException
     */
    private String createErrorResponse() throws JsonProcessingException {
        ApiResponse body = new ApiResponse();
        body.setCode(ErrorEnum.INVALID_REQUEST.getErrorCode());
        body.setMessage("无效客户端");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(body);

    }

    /**
     * Decodes the header into a username and password.
     *
     * @throws BadCredentialsException if the Basic header is not present or is not valid
     *                                 Base64
     */
    private String[] extractAndDecodeHeader(String header, HttpServletRequest request)
            throws IOException {

        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }
}

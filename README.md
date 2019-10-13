# 项目介绍

springcloud-oauth2搭建基于spring-cloud-starter-oauth2的认证中心和资源服务器的微服务项目，项目不仅仅简单的demo，项目的出发点在于实战应用，我在某司用的就是这个。
这个是我花了时间和精力整理出来的，只需要稍微调整就可应用于实际项目当中，并且项目包含大量注释，不仅可以让你会用，也可让你了解到一些流程、一些原理上的东西。
认证中心完成密码模式、授权码模式、刷新token模式、简化模式、以及自定义的手机号验证码模式。

> 如果大家有什么疑问和不懂的地方可以[issue](https://github.com/yaohw007/springcloud-oauth2/issues/new) 里提问。
有什么说得不对或不合理的地方也欢迎指出。如果对你有帮助，随手一个star可好^_^?

## 开发环境

- **JDK 1.8 +**
- **Maven 3.5 +**
- **IntelliJ IDEA ULTIMATE 2018.2 +** (*注意：务必使用 IDEA 开发，同时保证安装 `lombok` 插件*)
- **Redis 3.0 +**

## 运行方式

1. `git clone https://https://github.com/yaohw007/springcloud-oauth2.git`
2. 使用 IDEA 打开 clone 下来的项目
3. 项目启动顺序: eureka-server > auth-server > resource-server
> 注意：auth-server依赖redis服务，记得先启动redis服务哦~

## 认证中心部分代码

### AuthorizationConfig.java
```java
/**
 * 认证配置
 * @author: yaohw
 * @create: 2019-09-30 16:12
 **/
@Configuration
@EnableAuthorizationServer
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ClientDetailsServiceImpl clientDetailsService;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;




    /**
     * 配置token存储，这个配置token存到redis中
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    /**
     * 配置授权码模式授权码服务,不配置默认为内存模式
     * @return
     */
    @Primary
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new RedisAuthorizationCodeServices(redisConnectionFactory);
    }

    /**
     * 配置客户端详情
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //采用token转jwt，并添加一些自定义信息（token增强）（有默认非必须）
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(jwtAccessTokenConverter(),tokenEnhancer()));
        endpoints.tokenEnhancer(tokenEnhancerChain)
                //配置token存储，一般配置redis存储
                .tokenStore(tokenStore())
                //配置认证管理器
                .authenticationManager(authenticationManager)
                //配置用户详情server，密码模式必须
                .userDetailsService(userDetailsService)
                //配置授权码模式授权码服务,不配置默认为内存模式
                .authorizationCodeServices(authorizationCodeServices())
                //配置grant_type模式，如果不配置则默认使用密码模式、简化模式、验证码模式以及刷新token模式，如果配置了只使用配置中，默认配置失效
                //具体可以查询AuthorizationServerEndpointsConfigurer中的getDefaultTokenGranters方法
                .tokenGranter(tokenGranter(endpoints));
        // 配置TokenServices参数
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        // 是否支持刷新Token
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
        //设置accessToken和refreshToken的默认超时时间(如果clientDetails的为null就取默认的，如果clientDetails的不为null取clientDetails中的)
        tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(2));
        tokenServices.setRefreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30));
        endpoints.tokenServices(tokenServices);

    }



    /**
     * jwt格式封装token
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //设置jwt加解密秘钥，不设置会随机一个
        jwtAccessTokenConverter.setSigningKey("yaohw");
        return jwtAccessTokenConverter;
    }

    /**
     * token增强,添加一些元信息
     *
     * @return TokenEnhancer
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            final Map<String, Object> additionalInfo = new HashMap<>(2);
            additionalInfo.put("license", "yaohw");
            UserDetailImpl user = (UserDetailImpl) authentication.getUserAuthentication().getPrincipal();
            if (user != null) {
                additionalInfo.put("username", user.getUsername());
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        };
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .allowFormAuthenticationForClients()
                .tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("permitAll()");
    }

    /**
     * 创建grant_type列表
     * @param endpoints
     * @return
     */
    private TokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> list = new ArrayList<>();
        //这里配置密码模式、刷新token模式、自定义手机号验证码模式、授权码模式、简化模式
        list.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        list.add(new RefreshTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        list.add(new MobileCodeTokenGranter(authenticationManager,endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        list.add(new AuthorizationCodeTokenGranter(endpoints.getTokenServices(),endpoints.getAuthorizationCodeServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        list.add(new ImplicitTokenGranter(endpoints.getTokenServices(),endpoints.getClientDetailsService(),endpoints.getOAuth2RequestFactory()));
        return new CompositeTokenGranter(list);
    }
}
```
### SecurityConfigurerAdapter.java
```java

/**
 * security web安全配置,spring-cloud-starter-oauth2依赖于security
 *  默认情况下SecurityConfigurerAdapter执行比ResourceServerConfig先
 * @author: yaohw
 * @create: 2019-09-25 16:49
 */
@Configuration
@EnableWebSecurity
public class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 配置认证管理器
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    /**
     * 配置密码加密对象（解密时会用到PasswordEncoder的matches判断是否正确）
     * 用户的password和客户端clientSecret用到，所以存的时候存该bean encode过的密码
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 这里是对认证管理器的添加配置
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(provider())
                .userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**","/static/**");
    }

    /**
     *  安全请求配置,这里配置的是security的部分，这里配置全部通过，安全拦截在资源服务的配置文件中配置，
     *  要不然访问未验证的接口将重定向到登录页面，前后端分离的情况下这样并不友好，无权访问接口返回相关错误信息即可
     * @param http
     * @return void
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin().loginPage("/login")
                .permitAll()
                .and().authorizeRequests().anyRequest().permitAll()
                .and().csrf().disable().cors();
    }


    /**
     * 自定义手机验证码认证提供者
     *
     * @return
     */
    @Bean
    public MobileCodeAuthenticationProvider provider() {
        MobileCodeAuthenticationProvider provider = new MobileCodeAuthenticationProvider();
        provider.setStringRedisTemplate(stringRedisTemplate);
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

}
```
### ResourceServerConfig.java
```java
/**
 * 资源服务配置
 * @author: yaohw
 * @create: 2019-10-08 10:04
 **/
@Configuration
//启用资源服务
@EnableResourceServer
//启用方法级权限控制
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "auth-server";


    /**
     *  配置资源接口安全，http.authorizeRequests()针对的所有url，但是由于登录页面url包含在其中，这么配置会进行token校验，校验不通过返回错误json，
     *  而授权码模式获取code时需要重定向登录页面，重定向过程并不能携带token，所有不能用http.authorizeRequests()，
     *  而是用requestMatchers().antMatchers("")，这里配置的是需要资源接口拦截的url数组
     * @param http
     * @return void
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http    //配置需要保护的资源接口
                .requestMatchers().antMatchers("/user","/test/need_token","/update","/logout","/test/need_admin")
                .and().authorizeRequests().anyRequest().authenticated();
    }


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID).stateless(true);
    }
}
```
## 资源服务配置文件
```yml
spring:
  application:
    name: resource-server

server:
  port: 8003

#服务器发现注册配置
eureka:
  client:
    serviceUrl:
      #配置服务中心(可配置多个,用逗号隔开)
      defaultZone: http://admin:admin@localhost:9000/eureka/

##安全配置##
security:
  oauth2:
    resource:
      id: resource-server
      ## user-info-uri和token-info-uri二选择即可
      ##如果配置了user-info-uri，该资源服务器使用userInfoTokenServices远程调用认证中心接口，通过认证中心的OAuth2AuthenticationProcessingFilter完成验证工作，一般设置user-info-uri即可
      user-info-uri: http://127.0.0.1:8001/user
      prefer-token-info: false
      ## 该资源服务器使用RemoteTokenServices远程调用认证中心接口，注意一点就是如果使用token-info-uri那么就必须设置上clientId和clientSecret，通过CheckTokenEndpoint完成验证工作
      #token-info-uri: http://127.0.0.1:8001/oauth/check_token
    #client:
      #client-secret: yaohw
      #client-id: yaohw
```

## 部分源代码讲解
### 认证（获取token）TokenEndpoint.java
```java

	@RequestMapping(value = "/oauth/token", method=RequestMethod.POST)
	public ResponseEntity<OAuth2AccessToken> postAccessToken(Principal principal, @RequestParam
	Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {

		if (!(principal instanceof Authentication)) {
			throw new InsufficientAuthenticationException(
					"There is no client authentication. Try adding an appropriate authentication filter.");
		}

		// 根据当前请求获取到clientId
		String clientId = getClientId(principal);

		//获取当前ClientDetailsService（就是我们在AuthorizationConfig中配置）然后根据clientId去数据库查询客户端详情
		ClientDetails authenticatedClient = getClientDetailsService().loadClientByClientId(clientId);

		// 将请求参数封装成TokenRequest
		TokenRequest tokenRequest = getOAuth2RequestFactory().createTokenRequest(parameters, authenticatedClient);
		// 请求的clientId与查出来的匹配
		if (clientId != null && !clientId.equals("")) {
			// Only validate the client details if a client authenticated during this
			// request.
			if (!clientId.equals(tokenRequest.getClientId())) {
				// double check to make sure that the client ID in the token request is the same as that in the
				// authenticated client
				throw new InvalidClientException("Given client ID does not match authenticated client");
			}
		}
		// 校验客户端范围
		if (authenticatedClient != null) {
			oAuth2RequestValidator.validateScope(tokenRequest, authenticatedClient);
		}
		if (!StringUtils.hasText(tokenRequest.getGrantType())) {
			throw new InvalidRequestException("Missing grant type");
		}
		// 判断是否是简化模式（简化模式不是这个接口，走的是AuthorizationEndpoint类下的/oauth/authorize）
		if (tokenRequest.getGrantType().equals("implicit")) {
			throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
		}
		// 判断是否简化模式，如果是，清空返回，因为简化模式在第一步获取code的时候就将client信息缓存起来的，后面检验的是从缓存取出来补充完整
		if (isAuthCodeRequest(parameters)) {
			// The scope was requested or determined during the authorization step
			if (!tokenRequest.getScope().isEmpty()) {
				logger.debug("Clearing scope of incoming token request");
				tokenRequest.setScope(Collections.<String> emptySet());
			}
		}
		// 是否刷新token模式
		if (isRefreshTokenRequest(parameters)) {
			// A refresh token has its own default scopes, so we should ignore any added by the factory here.
			tokenRequest.setScope(OAuth2Utils.parseParameterList(parameters.get(OAuth2Utils.SCOPE)));
		}
		// 这步是整个认证的关键，这里简单说下流程，首先她会根据当前请求的grantType找到对应的认证模式，
		// 比如密码模式的ResourceOwnerPasswordTokenGranter，

		// 然后对应的AbstractTokenGranter调用对应的grant方法，grant方法中又调用经过一系列调用,
		// 在getOAuth2Authentication方法中生成对应的AbstractAuthenticationToken，比如UsernamePasswordAuthenticationToken，

		// 然后认证管理器（就是我们在AuthorizationConfig中配置的AuthenticationManager）调用认证方法						// authenticationManager.authenticate(abstractAuthenticationToken)

		// AbstractAuthenticationToken和AuthenticationProvider是存在一一对应的关系

		// 比如UsernamePasswordAuthenticationToken和DaoAuthenticationProvider，authenticationManager.authenticate()会根据传入的			// AbstractAuthenticationToken找到对应的AuthenticationProvider，

		// 真正认证逻辑通过AuthenticationProvider来完成的，比如密码模式的DaoAuthenticationProvider，会去根据用户名查询出对应的用户，
		// 然后校验用户密码是否匹配，用户是否锁定过期等

		// 具体可查看DaoAuthenticationProvider和她继承的AbstractUserDetailsAuthenticationProvider

		// 理清上面的思路后，我们就可以自定义grantType,就是定义一个继承AbstractTokenGranter的类重写getOAuth2Authentication方法
		// 该方法里面会用到AbstractAuthenticationToken和AuthenticationProvider
		// 我们再分别定义一个类分别继承对应的类即可（大概思路，具体查看代码）
		OAuth2AccessToken token = getTokenGranter().grant(tokenRequest.getGrantType(), tokenRequest);
		if (token == null) {
			throw new UnsupportedGrantTypeException("Unsupported grant type: " + tokenRequest.getGrantType());
		}
		//这个没什么好说的，就是http请求响应体封装
		return getResponse(token);

	}
```
### 验证token 
了解过的同学应该知道OAuth2她有资源服务和认证中心服务，那么她怎么保护资源服务接口的呢？实际上不管认证中服务还是资源服务当请求的接口需要安全校验时都会被OAuth2ClientAuthenticationProcessingFilter所拦截，只是拦截后做了不同的处理（取决于ResourceServerTokenServices的实例）。
资源服务：拦截请求后会远程调用认证服务器的http://127.0.0.1:8001/user或http://127.0.0.1:8001/oauth/check_token，至于调用哪个取决于配置文件，如配置如下配置将远程调用http://127.0.0.1:8001/user（资源服务端我们也一般这么配置即可)
```yml
##安全配置##
security:
  oauth2:
    resource:
      id: resource-server
      user-info-uri: http://127.0.0.1:8001/user
      prefer-token-info: false
```

#### （拦截token校验）OAuth2ClientAuthenticationProcessingFilter.java
```java
@Override
public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException, IOException, ServletException {

        OAuth2AccessToken accessToken;
        try {
        // 获取当前token，需要注意一点这个虽然用到restTemplate，但实际上这里并没有发起远程调度，这里restTemplate是OAuth2RestTemplate的实例
        // 一路点进去你会发现他只是从上下文获取到accessToken
        accessToken = restTemplate.getAccessToken();
        } catch (OAuth2Exception e) {
        BadCredentialsException bad = new BadCredentialsException("Could not obtain access token", e);
        publish(new OAuth2AuthenticationFailureEvent(bad));
        throw bad;
        }
        try {
        // 这步是校验token的关键，这里tokenServices是ResourceServerTokenServices实例，这里做怎么样的操作取决于注入的				// ResourceServerTokenServices实例
        // 默认情况下ResourceServerTokenServices的实例是DefaultTokenServices
        // 认证中心使用的就是DefaultTokenServices，这个类做的就是tokenStore.readAccessToken(accessTokenValue)
        // 我们配置中心配置的tokenStore的是RedisTokenStore，所以实际上她做的就是从redis中读取出accessToken相关信息
        <!-- 分割线 --->
        // 上面说的DefaultTokenServices是认证中心token的处理，资源服务下：
        // 如果配置文件中配置的user-info-uri则ResourceServerTokenServices注入的实例将是UserInfoTokenServices的实例
        // 如果配置token-info-uri则ResourceServerTokenServices注入的实例将是RemoteTokenServices
        // 如果两者都配置了，优先UserInfoTokenServices
        // UserInfoTokenServices和RemoteTokenServices做的事都是远程调度认证中心相应的接口完成token的校验
        // 两者主要区别在于RemoteTokenServices需要配置clientId和clientSecret
        // RemoteTokenServices中有这么一句话：Null Client ID or Client Secret detected. Endpoint that requires authentication will reject 	  // request with 401 error.
        // 具体请查看RemoteTokenServices和UserInfoTokenServices
        OAuth2Authentication result = tokenServices.loadAuthentication(accessToken.getValue());
        if (authenticationDetailsSource!=null) {
        request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, accessToken.getValue());
        request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, accessToken.getTokenType());
        result.setDetails(authenticationDetailsSource.buildDetails(request));
        }
        publish(new AuthenticationSuccessEvent(result));
        return result;
        }
        catch (InvalidTokenException e) {
        BadCredentialsException bad = new BadCredentialsException("Could not obtain user details from token", e);
        publish(new OAuth2AuthenticationFailureEvent(bad));
        throw bad;
        }

        }
```

## postman接口测试截图
> 注意：postman的Authorization有几种方式，这里主要用到Basic的，
实际请求会在请求头加上一个字段名为Authorization，值为:Basic eWFvaHc6eWFvaHc=,这后面的其实是yaohw:yaohw,经过base64加密了一下，
携带token的请求要求在请求头加上一个字段名为Authorization，值为Bearer accessToken。
### 密码模式

![image](./images/密码模式_01.png "密码模式")

### 自定义手机号验证码模式

> 注意：需要在redis中设置一个String类型的key为sms:code:你的手机号，值就是你的短信验证码的缓存

![image](./images/自定义模式_01.png "自定义模式")

### 授权码模式
##### 授权码模式步骤一

![image](./images/授权码模式_01.png "授权码模式步骤一")

##### 授权码模式步骤二

![image](./images/授权码模式_02.png "授权码模式步骤二")

### 简化模式

![image](./images/简化模式_01.png "简化")
### 刷新token模式

![image](./images/刷新token.png "刷新")


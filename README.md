# 项目介绍

springcloud-oauth2搭建基于springcloud-oauth2认证中心和资源服务器，项目不仅仅简单的demo，项目的出发点在于实战应用，我在某公司用的就是这个，
这个是我花了不少时间整理出来的，只需要稍微调整就可应用于实际项目当中，并且项目包含大量注释，不仅可以让你会用，也可让你了解到一些流程、一些原理上的东西。
认证中心完成密码模式、授权码模式、刷新token模式、简化模式、以及自定义的手机号验证码模式

> 如果大家有什么疑问和不懂的地方可以[issue](https://https://github.com/yaohw007/springcloud-oauth2/issues/new) 里提问。

## 开发环境

- **JDK 1.8 +**
- **Maven 3.5 +**
- **IntelliJ IDEA ULTIMATE 2018.2 +** (*注意：务必使用 IDEA 开发，同时保证安装 `lombok` 插件*)
- **Redis **

## 运行方式

1. `git clone https://https://github.com/yaohw007/springcloud-oauth2`
2. 使用 IDEA 打开 clone 下来的项目
3. 项目启动顺序: eureka-server > auth-server > resource-server
4. **`注意：auth-server依赖redis服务，记得先启动redis服务哦~`**

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
     *  配置资源接口安全，http.authorizeRequests()针对的所有url，但是由于登录页面url包含在其中，这里配置会进行token校验，校验不通过返回错误json，
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

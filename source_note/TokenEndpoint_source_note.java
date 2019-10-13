	
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
		// 这步是整个认证的关键，这里简单说下流程，首先她会根据当前请求的grantType找到对应的认证模式，比如密码模式的ResourceOwnerPasswordTokenGranter，

		// 然后对应的AbstractTokenGranter调用对应的grant方法，grant方法中又调用经过一系列调用，在getOAuth2Authentication方法中生成对应的AbstractAuthenticationToken，比如UsernamePasswordAuthenticationToken，

		// 然后认证管理器（就是我们在AuthorizationConfig中配置的AuthenticationManager）调用认证方法authenticationManager.authenticate(abstractAuthenticationToken)

		// AbstractAuthenticationToken和AuthenticationProvider是存在一一对应的关系

		// 比如UsernamePasswordAuthenticationToken和DaoAuthenticationProvider，authenticationManager.authenticate()会根据传入的AbstractAuthenticationToken找到对应的AuthenticationProvider，

		// 真正认证逻辑通过AuthenticationProvider来完成的，比如密码模式的DaoAuthenticationProvider，会去根据用户名查询出对应的用户，然后校验用户密码是否匹配，用户是否锁定过期等

		// 具体可查看DaoAuthenticationProvider和她继承的AbstractUserDetailsAuthenticationProvider

		// 理清上面的思路后，我们就可以自定义grantType,就是定义一个继承AbstractTokenGranter的类，重写getOAuth2Authentication方法，该方法里面会用到AbstractAuthenticationToken和AuthenticationProvider
		// 我们再分别一个类分别继承对应的类即可（大概思路，具体查看代码）
		OAuth2AccessToken token = getTokenGranter().grant(tokenRequest.getGrantType(), tokenRequest);
		if (token == null) {
			throw new UnsupportedGrantTypeException("Unsupported grant type: " + tokenRequest.getGrantType());
		}
		//这个没什么好说的，就是http请求响应体封装
		return getResponse(token);

	}
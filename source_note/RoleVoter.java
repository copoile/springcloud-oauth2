/***
 * 方法级-权限校验
 */
public int vote(Authentication authentication, Object object,
			Collection<ConfigAttribute> attributes) {
		// 这里的Authentication是经过OAuth2ClientAuthenticationProcessingFilter过滤的Authentication
		// 如果等于null 返回拒绝编码
		if (authentication == null) {
			return ACCESS_DENIED;
		}
		// 赋值弃用编码，也就是我们方法那里没加有对应的用户权限注解
		int result = ACCESS_ABSTAIN;
		// 取出token中的权限列表
		Collection<? extends GrantedAuthority> authorities = extractAuthorities(authentication);

		//
		for (ConfigAttribute attribute : attributes) {
			if (this.supports(attribute)) {
				result = ACCESS_DENIED;

				// Attempt to find a matching granted authority
				for (GrantedAuthority authority : authorities) {
					if (attribute.getAttribute().equals(authority.getAuthority())) {
						return ACCESS_GRANTED;
					}
				}
			}
		}

		return result;
	}
package mkn;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends ResourceServerConfigurerAdapter {
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/actuator/**", "/jwt").permitAll()
			.anyRequest().authenticated();
	}
	
	// Spring Security uses a default resourceId (oauth2-resource) which our JWT does not have. 
	// So we need to null it out so that the logic that verifies it will be bypassed.
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(null);
	}
	
  @Bean
  @Profile("custom")
  public TokenStore jwkTokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
    DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
    tokenConverter.setUserTokenConverter(new CustomUserAuthenticationConverter());
    jwtAccessTokenConverter.setAccessTokenConverter(tokenConverter);
    return new JwtTokenStore(jwtAccessTokenConverter);
  }
}

package mkn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

// This class uses "custom" fields in the JWT versus the ones Spring Security Expects
public class CustomUserAuthenticationConverter implements UserAuthenticationConverter {
	public static final String ADMIN = "ADMIN";

	private static final String USERID_FIELD = "name";
	
	@Override
	public Map<String, ?> convertUserAuthentication(Authentication authentication) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put(USERNAME, authentication.getName());
		if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
			response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
		}
		return response;
	}

	@Override
	public Authentication extractAuthentication(Map<String, ?> map) {
		if (map.containsKey(USERID_FIELD)) {
			Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
			UserDetails principal = new User((String) map.get(USERID_FIELD), "N/A", true, true, true, true, authorities);
			return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
		}
		return null;
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
		if (!map.containsKey("admin")) {
			return new ArrayList<>();
		}

		Object authorities = map.get("admin");
		if (authorities instanceof Boolean) {
			if (((Boolean) authorities)) {
			}
		}
		return new ArrayList<>();
	}

}

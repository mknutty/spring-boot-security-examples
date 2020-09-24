package mkn;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExampleService {

	// Has access
	public boolean demo4() {
		log.info("Service called");
		return true;
	}
	
	// Does not have access
	public boolean demo5() {
		log.info("Service called");
		return false;
	}
	
	// Has access to someValue == 1 (good for checking if they have access to resource)
	public boolean demo6(Principal principal, long someValue) {
		log.info("Principal is " + principal.getName());
		return someValue == 1;
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	public void demo10() {
		log.info("Service Preauthorized");
	}
	
	@PreAuthorize("hasAuthority('NOTFORYOU')")
	public void demo11() {
		log.info("Service Preauthorized");
	}
}

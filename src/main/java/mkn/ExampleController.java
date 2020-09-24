package mkn;

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
public class ExampleController {
	private ExampleService demoService;
	
	// Use this to get a jwt to pass as a bearer token
	@SneakyThrows
	@GetMapping("jwt")
	public String jwt() {
	  return Jwts.builder()
        .setSubject("1234567890")
        .setId("09ac71ba-d4f3-4ffa-a9b7-8f54eba2a22b")
        .setIssuedAt(Date.from(Instant.now()))
        .setExpiration(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
        .claim("name", "John Doe") //For CustomUserAuthenticationConverter 
        .claim("admin", true) //For CustomUserAuthenticationConverter
        .claim("user_name", "jdoe") //For DefaultUserAuthenticationConverter
        .claim("authorities", "ADMIN") //For DefaultUserAuthenticationConverter
        .signWith(SignatureAlgorithm.HS256, "secret".getBytes("UTF-8"))
        .compact();
	}

	// Spring injects the Authentication object into the method
	@GetMapping(path = "demo1")
	public String demo1(Authentication authentication) {
		log.info("Authentication - "  + authentication);
		return "Worked!";
	}
	
	//Spring injects the Principal object into the method
	@GetMapping(path = "demo2")
	public String demo2(Principal principal) {
		log.info("Principal - " + principal);
		return "Worked!";
	}
	
	@GetMapping(path = "demo3")
	public String demo3() {
		// This will be useful if Envers is used. An implementer of RevisionListener can get the current user.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("Authentication from SecurityContextHolder - " + authentication);
		return "Worked!";
	}
	
	//Uses a service call to determine access 
	@GetMapping(path = "demo4")
	@PreAuthorize("@demoService.demo4()")
	public String demo4() {
		return "Worked!";
	}
	
	//Uses a service call to determine access
	@GetMapping(path = "demo5")
	@PreAuthorize("@demoService.demo5()")
	public String demo5() {
		return "Won't work!";
	}

	// The principal object is passed via SPEL to a service
	// Will work if someValue == 1
	@GetMapping(path = "demo6")
	@PreAuthorize("@demoService.demo6(#principal, #someValue)")
	public String demo6(Principal principal, Long someValue) {
		return "Worked!";
	}
	
	// Ensures the user is an ADMIN before calling the method.
	@GetMapping(path = "demo7")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String demo7() {
		return "Worked!";
	}
	
	//Ensures the user is an NOTFORYOU before calling the method.
	@GetMapping(path = "demo8")
	@PreAuthorize("hasAuthority('NOTFORYOU')")
	public String demo8() {
		return "Won't work!";
	}
	
	//Ensures the user is an ADMIN or NOTFORYOU before calling the method.
	@GetMapping(path = "demo9")
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('NOTFORYOU')")
	public String demo9() {
		return "Worked!";
	}
	
	
	//Ensures the user is an ADMIN before calling the service method (see the service class).
	@GetMapping(path = "demo10")
	public String demo10() {
		demoService.demo10();
		return "Worked!";
	}

  //Ensures the user is an NOTFORYOU before calling the service method (see the service class)	
	@GetMapping(path = "demo11")
	public String demo11() {
		demoService.demo11();
		return "Won't work!";
	}
	
	//Ensures the returning String is 'you'. SPEL can be used on complex objects
	@PostAuthorize("returnObject == 'you'")
	@GetMapping(path = "demo12")
	public String demo12() {
		return "you";
	}
	
	//Ensures the returning String is 'you'.
	@PostAuthorize("returnObject == 'you'")
	@GetMapping(path = "demo13")
	public String demo13() {
		return "not you";
	}
	
	// This is good for you can't edit you.
	// Pass comma separated list of names.
	@PreFilter("filterObject != 'bob'")
	@GetMapping(path = "demo14")
	public List<String> demo14(@RequestParam("userNames") List<String> userNames) {
	    return userNames;
	}
	
	// This is good for you can't get you.
	@PostFilter("filterObject != 'bob'")
	@GetMapping(path = "demo15")
	public List<String> demo15() {
		List<String> list = new ArrayList<>();
		list.add("sally");
		list.add("bob");
		list.add("fred");
	  return list;
	}
	
	// Uses a meta Annotation to ensure the user is ADMIN. It works just like demo7.
	@GetMapping(path = "demo16")
	@IsAdmin
	public String demo16() {
		return "Worked!";
	}
	
	// Uses an expression to extract a value. This is useful because Authentication and Principal are interfaces. 
	// This only works with the custom CustomUserAuthenticationConverter.
	@GetMapping(path = "demo17")
	public String demo17(@AuthenticationPrincipal(expression="username") String name) {
		log.info("User is " + name);
		return "Worked!";
	}
	
}

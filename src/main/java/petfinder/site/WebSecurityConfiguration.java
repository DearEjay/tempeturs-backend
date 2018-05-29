package petfinder.site;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by jlutteringer on 8/22/17.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    http.cors().and().csrf().disable();
		/*http
				.authorizeRequests()
					.antMatchers("/").permitAll()
					.antMatchers("/statics/**").permitAll()
				.anyRequest().authenticated()
					.and()
				.formLogin()
					
					.permitAll()
					.and()
				.logout()
					.permitAll();*/
	    
	    ///http.authorizeRequests()
       // .anyRequest().permitAll().and()
	   // .
	}
	
	

}


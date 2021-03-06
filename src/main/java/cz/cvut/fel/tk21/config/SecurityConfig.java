package cz.cvut.fel.tk21.config;

import cz.cvut.fel.tk21.filter.JwtRequestFilter;
import cz.cvut.fel.tk21.rest.handler.RestAccessDeniedHandler;
import cz.cvut.fel.tk21.rest.handler.RestAuthenticationEntryPoint;
import cz.cvut.fel.tk21.service.security.UserDetailsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userService;
    private final JwtRequestFilter jwtRequestFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public SecurityConfig(UserDetailsService userService, JwtRequestFilter jwtRequestFilter, RestAuthenticationEntryPoint restAuthenticationEntryPoint, RestAccessDeniedHandler restAccessDeniedHandler) {
        this.userService = userService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        //Development only
        /*http.headers().frameOptions().disable();
        http.authorizeRequests()
                .antMatchers("/h2-console/*")
                .permitAll();*/

        http.authorizeRequests()
                .antMatchers("/websocket/**")
                .permitAll();

        http.authorizeRequests()
                .antMatchers("/*", "/static/**",
                "/api/authenticate", "/api/user", "/api/logout", "/api/confirm", "/api/reservation/**")
                .permitAll();

        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/password/forget", "/api/passwordChange/*")
                .permitAll();

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/club/**")
                .permitAll();

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/post/**")
                .permitAll();

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/download/**")
                .permitAll();

        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/invitation/professional/**")
                .permitAll();

        http.authorizeRequests().anyRequest().authenticated();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.exceptionHandling().accessDeniedHandler(restAccessDeniedHandler).authenticationEntryPoint(restAuthenticationEntryPoint);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}

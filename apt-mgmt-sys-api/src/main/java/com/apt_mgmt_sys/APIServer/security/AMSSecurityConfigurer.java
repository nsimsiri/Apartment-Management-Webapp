package com.apt_mgmt_sys.APIServer.security;

import com.apt_mgmt_sys.APIServer.services.AuthorityService;
import com.apt_mgmt_sys.APIServer.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class AMSSecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorityService authService;

    @Override
    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
        authManagerBuilder.authenticationProvider(customAuthenticationProvider());
    }

    @Override
    public UserDetailsService userDetailsServiceBean(){
        return new AMSUserDetailsService(userService, authService);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .cors()
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/users/").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(getRestAuthenticationSuccessHandler())
                .failureHandler(getRestOnAuthenticationFailureHandler())
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .deleteCookies("JSESSIONID").invalidateHttpSession(true);
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.ignoring()
                .antMatchers("/test/**")
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers(HttpMethod.GET, "/ping")
                .antMatchers(HttpMethod.POST ,"/users/")
                .antMatchers(HttpMethod.POST, "/users")
                .antMatchers(HttpMethod.POST, "/users/update/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider customAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsServiceBean());
        return provider;
    }

    @Bean
    public RestOnLoginSuccessHandler getRestAuthenticationSuccessHandler(){
        return new RestOnLoginSuccessHandler(userService);
    }

    @Bean
    AuthenticationFailureHandler getRestOnAuthenticationFailureHandler(){
        return new RestOnAuthenticationFailureHandler();
    }



}

package com.pbs.tech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * @author Muralidharan A
 * @created 31/01/2022 - 16:29
 */

@EnableWebSecurity
public class SecurityConfiguration {

    @Order(1)
    @Configuration
    public static class UserApiSecurity extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/v1/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable() // we don't need CSRF because our token is invulnerable
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().disable()
                    .addFilterBefore(new SecurityFilter(), BasicAuthenticationFilter.class)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }


    }

    @Order(3)
    @Configuration
    public static class UserWebSecurity extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/swagger-ui/**", "/javainuse-openapi/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic();
        }
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("javainuse")
                    .password(passwordEncoder().encode("javainuse"))
                    .authorities("ADMIN");
        }
    }

    @Order(2)
    @Configuration
    public static class WebConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.exceptionHandling().accessDeniedPage("/403");
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/login","/auth")
                    .permitAll()
                    .and()
                    .logout()
                    .permitAll()
                    .and()
                    .headers().frameOptions().sameOrigin()
                    .and()
                    .authorizeRequests().antMatchers("/**").authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .successForwardUrl("/")
                    .permitAll();

            http.authorizeRequests().antMatchers("/admin/**").hasAnyRole("ADMIN");

        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers(HttpMethod.GET,
                    "/vendor/**",
                    "/web/**");
        }
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

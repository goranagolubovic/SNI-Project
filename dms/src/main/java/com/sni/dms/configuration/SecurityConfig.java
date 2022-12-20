//package com.sni.dms.configuration;
//
//import lombok.SneakyThrows;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
//@Configuration
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @SneakyThrows
//    @Override
//    protected void configure(HttpSecurity http) {
//        http
//        .csrf().disable()
//        .authorizeRequests().anyRequest().authenticated()
//        .and()
//        .oauth2ResourceServer().jwt();
//
//        // Send a 401 message to the browser (w/o this, you'll see a blank page)
//       // Okta.configureResourceServer401ResponseBody(http);
//        }
//        }
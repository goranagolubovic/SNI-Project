package com.sni.dms.configuration;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@KeycloakConfiguration
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeRequests()
//                .antMatchers("/public/**").permitAll()
//                .antMatchers("/member/**").hasAnyRole("member")
//                .antMatchers("/moderator/**").hasAnyRole("moderator")
                .antMatchers("/auth").permitAll()
                .antMatchers("/admin/*").hasRole("admin")
                .antMatchers("/files/parentDir").hasAnyRole("admin","document_admin","client")
                .antMatchers("/files/availableDirs").hasRole("document_admin")
                .antMatchers("/admin/*").hasRole("admin")
                .antMatchers("/files/all").hasAnyRole("admin","client","document_admin")
                .antMatchers("/files/upload").hasAnyRole("create","document_admin")
                .antMatchers("/files/read").hasAnyRole("admin","read","document_admin")
                .antMatchers("/files/edit").hasAnyRole("update","document_admin")
                .antMatchers("/files/delete").hasAnyRole("delete","document_admin")
                .antMatchers("/logs").hasRole("admin")
//                .antMatchers("/role").hasAnyRole("admin","document_admin","client")
//                .antMatchers("/info").hasAnyRole("admin","document_admin","client")
//                .antMatchers("/changePassword").hasAnyRole("admin","document_admin","client")
                .anyRequest().permitAll();
        http.csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }


}
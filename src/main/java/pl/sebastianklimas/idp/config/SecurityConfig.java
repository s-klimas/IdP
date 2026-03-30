package pl.sebastianklimas.idp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.web.authentication.ClientSecretBasicAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.ClientSecretPostAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, authServer -> authServer
                        .clientAuthentication(clientAuth -> clientAuth
                                .authenticationConverter(new ClientSecretBasicAuthenticationConverter())
                                .authenticationConverter(new ClientSecretPostAuthenticationConverter()))
                        .oidc(Customizer.withDefaults())
                )
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(authorizationServerConfigurer.getEndpointsMatcher())
                )
                .formLogin(Customizer.withDefaults());

        return http.build();
    }
}

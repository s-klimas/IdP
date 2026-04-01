package pl.sebastianklimas.idp.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.authorization.web.authentication.ClientSecretBasicAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.ClientSecretPostAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
public class SecurityConfig {
    @Bean
    @Order(1) // endpointy OAuth2
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

    @Bean
    @Order(2) // endpoint do dodawania klientów dostępny tylko lokalnie
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        IpAddressMatcher ipv4Matcher = new IpAddressMatcher("127.0.0.1");
        IpAddressMatcher ipv6Matcher = new IpAddressMatcher("::1");

        http
                .securityMatcher("/admin/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**").access( (authentication, context) -> {
                            if (context == null) return new AuthorizationDecision(false);

                            HttpServletRequest request = context.getRequest();

                            boolean allowed = ipv4Matcher.matches(request) || ipv6Matcher.matches(request);

                            return new AuthorizationDecision(allowed);
                        })
                        .anyRequest().denyAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}

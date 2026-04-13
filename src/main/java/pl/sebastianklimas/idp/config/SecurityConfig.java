package pl.sebastianklimas.idp.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.ClientSecretBasicAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.ClientSecretPostAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import pl.sebastianklimas.idp.auth.PasswordGrantAuthenticationConverter;
import pl.sebastianklimas.idp.auth.PasswordGrantAuthenticationProvider;
import pl.sebastianklimas.idp.users.UserRepository;

@Configuration
public class SecurityConfig {
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            OAuth2AuthorizationService authorizationService,
            OAuth2TokenGenerator<?> tokenGenerator) throws Exception {

        http
                .oauth2AuthorizationServer(authorizationServer -> {
                    http.securityMatcher(authorizationServer.getEndpointsMatcher());
                    authorizationServer
                            .clientAuthentication(clientAuth -> clientAuth
                                    .authenticationConverter(new ClientSecretBasicAuthenticationConverter())
                                    .authenticationConverter(new ClientSecretPostAuthenticationConverter()))
                            .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                                    .accessTokenRequestConverter(new PasswordGrantAuthenticationConverter())
                                    .authenticationProvider(new PasswordGrantAuthenticationProvider(
                                            userRepository, passwordEncoder,
                                            authorizationService, tokenGenerator)))
                            .oidc(Customizer.withDefaults());
                })
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/oauth2/**")
                );

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

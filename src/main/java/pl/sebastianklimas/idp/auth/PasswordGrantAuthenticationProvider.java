package pl.sebastianklimas.idp.auth;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import pl.sebastianklimas.idp.users.Role;
import pl.sebastianklimas.idp.users.User;
import pl.sebastianklimas.idp.users.UserRepository;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PasswordGrantAuthenticationProvider implements AuthenticationProvider {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<?> tokenGenerator;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        PasswordGrantAuthenticationToken token = (PasswordGrantAuthenticationToken) authentication;

        User user = userRepository.findByEmail(token.getPrincipal().toString())
                .filter(u -> passwordEncoder.matches((String) token.getCredentials(), u.getPassword()))
                .orElseThrow(() -> new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT));

        RegisteredClient registeredClient = token.getRegisteredClient();

        Set<String> authorities = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        OAuth2TokenContext tokenContext = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        authorities.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toSet())
                ))
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(token.getScopes())
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .build();

        OAuth2Token generatedToken = tokenGenerator.generate(tokenContext);
        if (generatedToken == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.SERVER_ERROR);
        }

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                generatedToken.getTokenValue(),
                generatedToken.getIssuedAt(),
                generatedToken.getExpiresAt(),
                token.getScopes()
        );

        OAuth2Authorization authorization = OAuth2Authorization
                .withRegisteredClient(registeredClient)
                .principalName(user.getEmail())
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .accessToken(accessToken)
                .attribute(Principal.class.getName(), tokenContext.getPrincipal())
                .build();

        authorizationService.save(authorization);

        return new PasswordGrantAuthenticationToken(
                user.getEmail(),
                registeredClient,
                token.getScopes(),
                tokenContext.getPrincipal().getAuthorities()
        );
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return PasswordGrantAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

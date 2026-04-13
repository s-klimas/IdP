package pl.sebastianklimas.idp.auth;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class PasswordGrantAuthenticationToken extends AbstractAuthenticationToken {
    private final String username;
    private final String password;
    @Getter
    private final RegisteredClient registeredClient;
    @Getter
    private final Set<String> scopes;

    public PasswordGrantAuthenticationToken(
            String username,
            String password,
            RegisteredClient registeredClient,
            Set<String> scopes
    ) {
        super(Collections.emptyList());
        this.username = username;
        this.password = password;
        this.registeredClient = registeredClient;
        this.scopes = scopes;
        setAuthenticated(false);
    }

    public PasswordGrantAuthenticationToken(
            String username,
            RegisteredClient registeredClient,
            Set<String> scopes,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(authorities);
        this.username = username;
        this.password = null;
        this.registeredClient = registeredClient;
        this.scopes = scopes;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}

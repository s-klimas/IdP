package pl.sebastianklimas.idp.auth;


import jakarta.servlet.http.HttpServletRequest;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PasswordGrantAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grandType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!"password".equals(grandType)) {
            return null;
        }

        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        if (!(clientPrincipal instanceof OAuth2ClientAuthenticationToken clientAuth)
                || !clientAuth.isAuthenticated()) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }

        Set<String> scopes = new HashSet<>();
        String scopeParam = request.getParameter(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scopeParam)) {
            scopes = new HashSet<>(Arrays.asList(scopeParam.split(" ")));
        }

        return new PasswordGrantAuthenticationToken(
                username, password, clientAuth.getRegisteredClient(), scopes
        );
    }
}

package pl.sebastianklimas.idp.admin;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Service;
import pl.sebastianklimas.idp.admin.dto.CreateClientRequest;
import pl.sebastianklimas.idp.admin.dto.CreateClientResponse;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminService {

    private final PasswordEncoder passwordEncoder;
    private final RegisteredClientRepository registeredClientRepository;

    public CreateClientResponse registerClient(CreateClientRequest createClientRequest) {

        String clientId = UUID.randomUUID().toString();
        String rawSecret = generateSecureSecret();

        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(rawSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientName(createClientRequest.getClientName())
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("")
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(false)
                        .requireAuthorizationConsent(false)
                        .build())
                .build();

        registeredClientRepository.save(client);

        return new CreateClientResponse(clientId, rawSecret);
    }

    public String generateSecureSecret() {
        return "";
    }
}

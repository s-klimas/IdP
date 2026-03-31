INSERT INTO oauth2_registered_client (id,
                                      client_id,
                                      client_secret,
                                      client_name,
                                      client_authentication_methods,
                                      authorization_grant_types,
                                      scopes,
                                      client_settings,
                                      token_settings
)
VALUES ('518b8e70-49bb-4650-93e9-7273ec50b827',
        '0cb8abe1-73b7-4d61-aa72-8c2b246cc427',
        '{noop}5LJEdDR6V1hne75_X6igDn6uPUpz_guIPo6Qke-fX9M',
        'DevClient',
        'client_secret_basic',
        'client_credentials',
        'read,write',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration","PT5M"],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration","PT1H"],"settings.token.authorization-code-time-to-live":["java.time.Duration","PT5M"],"settings.token.device-code-time-to-live":["java.time.Duration","PT5M"]}'
       );

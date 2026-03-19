package pl.sebastianklimas.idp.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateClientResponse {
    String clientId;
    String rawSecret;
}

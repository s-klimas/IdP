package pl.sebastianklimas.idp.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CreateClientRequest {
    private String clientName;
}

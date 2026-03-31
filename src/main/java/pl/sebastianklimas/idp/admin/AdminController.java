package pl.sebastianklimas.idp.admin;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sebastianklimas.idp.admin.dto.CreateClientRequest;
import pl.sebastianklimas.idp.admin.dto.CreateClientResponse;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Deprecated
    @PostMapping("/register-client")
    public CreateClientResponse register(@RequestBody CreateClientRequest createClientRequest) {
        return adminService.registerClient(createClientRequest);
    }
}

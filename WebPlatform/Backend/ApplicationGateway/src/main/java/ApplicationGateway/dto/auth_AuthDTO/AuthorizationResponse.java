package ApplicationGateway.dto.auth_AuthDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationResponse {

    private Long userId;
    private Role role;
    private Boolean isAuthorized;
}
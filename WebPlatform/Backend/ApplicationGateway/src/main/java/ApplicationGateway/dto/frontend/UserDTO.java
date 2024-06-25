package ApplicationGateway.dto.frontend;

import ApplicationGateway.dto.auth_AuthDTO.Role;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class UserDTO {

    private String username;

    private String password;

    private String role;
}

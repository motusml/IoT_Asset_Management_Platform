package ApplicationGateway.dto.dataManagerDTO;

import ApplicationGateway.dto.auth_AuthDTO.Role;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class UserInfoDTO {
    private String username;
    private Role role;

    private long id;
}
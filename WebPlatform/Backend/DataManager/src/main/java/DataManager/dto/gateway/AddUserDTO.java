package DataManager.dto.gateway;

import DataManager.model.Role;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class AddUserDTO {

    private String username;

    private String password;

    private String role;
}

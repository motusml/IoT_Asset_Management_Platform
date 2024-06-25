package DataManager.dto.asset;

import DataManager.model.Role;
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

package ApplicationGateway.dto.frontend;

import ApplicationGateway.dto.dataManagerDTO.UserInfoDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class CompactUserDTO {

    public List<UserInfoDTO> users = new ArrayList<>();
}

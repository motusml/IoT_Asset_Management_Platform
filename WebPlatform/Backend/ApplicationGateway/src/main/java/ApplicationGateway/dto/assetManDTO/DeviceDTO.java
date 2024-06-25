package ApplicationGateway.dto.assetManDTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class DeviceDTO {

    private String place;

    private String type;

    private String status;

}
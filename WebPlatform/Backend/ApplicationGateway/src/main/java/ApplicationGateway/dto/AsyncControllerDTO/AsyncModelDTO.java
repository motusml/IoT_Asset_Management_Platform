package ApplicationGateway.dto.AsyncControllerDTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class AsyncModelDTO {

    private byte[] model;

    private String deviceId;
}

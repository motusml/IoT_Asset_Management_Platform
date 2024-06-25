package ApplicationGateway.dto.frontend;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class ModelDTO {

    private byte[] model;


    private String assetId;

    private String modelName;

}
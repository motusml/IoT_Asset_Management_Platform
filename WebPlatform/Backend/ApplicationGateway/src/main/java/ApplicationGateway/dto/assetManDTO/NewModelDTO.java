package ApplicationGateway.dto.assetManDTO;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
@ToString
@Getter
public class NewModelDTO {
    private byte[] model;

    private String modelName;

    private String assetId;

    private Boolean fromUser;

    public NewModelDTO (byte[] model, String modelName, String assetId, Boolean fromUser) {
        this.model = model;
        this.modelName = modelName;
        this.assetId = assetId;
        this.fromUser = fromUser;
    }
}

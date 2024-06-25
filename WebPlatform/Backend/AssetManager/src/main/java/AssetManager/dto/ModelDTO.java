package AssetManager.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class ModelDTO {

    private byte[] model;

    private String modelName;

    private String assetId;

    private Boolean fromUser;

}
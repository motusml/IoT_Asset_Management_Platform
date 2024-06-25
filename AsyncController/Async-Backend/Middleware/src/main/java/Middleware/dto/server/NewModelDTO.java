package Middleware.dto.server;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class NewModelDTO {

    private byte[] model;


    private String assetId;

    private String modelName;
}

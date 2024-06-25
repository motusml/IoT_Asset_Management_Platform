package Middleware.dto.server;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class ModelDTO {

    private byte[] model;

    private String deviceId;

}

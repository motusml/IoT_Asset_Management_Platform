package Middleware.dto.devices;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class SendModelDTO {

    private byte[] model;

    private String modelName;

    private String deviceName;
}

package Middleware.dto.server;

import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class DeviceDataDTO {
    private String deviceId;

    private Map<String, String> data;
}

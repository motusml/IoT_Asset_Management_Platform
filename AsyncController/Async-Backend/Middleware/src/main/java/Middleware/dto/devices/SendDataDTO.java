package Middleware.dto.devices;

import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class SendDataDTO {

    private String deviceName;

    private Map<String, String> data;
}

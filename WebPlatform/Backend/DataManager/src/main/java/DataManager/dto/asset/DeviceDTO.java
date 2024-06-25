package DataManager.dto.asset;

import lombok.*;

import java.time.LocalDate;

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

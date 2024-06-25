package DataManager.dto.asset;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class PendingDeviceDTO {

    boolean pendingData;

    boolean pendingRetrieve;

    boolean pendingSend;
}

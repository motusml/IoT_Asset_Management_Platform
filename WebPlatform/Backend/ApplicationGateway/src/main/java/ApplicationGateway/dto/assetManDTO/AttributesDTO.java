package ApplicationGateway.dto.assetManDTO;

import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class AttributesDTO {

    private Map<String, String> attributes;
}
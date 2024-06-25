package AssetManager.dto;

import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class RelationshipsDTO {

    private Map<String, String> relationships;
}

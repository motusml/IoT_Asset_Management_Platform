package AssetManager.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class RelNamesDTO {
    private List<String> relationships;
}

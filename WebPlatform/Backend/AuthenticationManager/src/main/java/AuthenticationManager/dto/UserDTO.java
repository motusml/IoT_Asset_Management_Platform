package AuthenticationManager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserDTO {
    @Schema(
            description = "ID of User",
            example = "999")
    private Long id;

    @Schema(
            description = "Username",
            example = "john")
    private String username;

    @Schema(
            description = "Password (in clear, for now)",
            example = "1234")
    private String password;

    @Schema(
            description = "all possible roles of the user",
            example = "ADMIN, TECHNICIAN")

    @Enumerated(EnumType.STRING)
    private Role role;


}

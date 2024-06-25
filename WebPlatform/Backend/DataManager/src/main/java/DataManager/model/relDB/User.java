package DataManager.model.relDB;

import DataManager.model.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = true, updatable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = true, updatable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User (String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}

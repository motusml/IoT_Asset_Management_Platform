package AuthenticationManager.repository;

import AuthenticationManager.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

//This class is responsible for retrieving user information from the data controller

@Service
@AllArgsConstructor
@Slf4j
public class UserRepository {
    @Value("${datamanager.address}")
    private String dataControllerAddress;

    @Value("${datamanager.port}")
    private int dataControllerPort;
    private final WebClient webClient;

    public UserRepository(){
        this.webClient= WebClient.builder().build();
    }

    public ResponseEntity<Optional<UserDTO>> retrieveUser(String username) {
        String p = String.format("http://%s:%d/user?", dataControllerAddress, dataControllerPort)
                + "username=" + username;
        Optional<UserDTO> userOptional = webClient.get()
                .uri(p)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .blockOptional();

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

package AuthenticationManager.service;


import AuthenticationManager.dto.*;
import AuthenticationManager.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    // 256 bit key
    private static final String SECRET_KEY = "Yi0mdJDYdU4qluYFSNpVE2ze4dm5s+3qzdQPnZtdMksd/subVSm2tvtdFzWO0TGC";
    private static final long EXPIRATION_TIME = 86_400_000; // 1 day in milliseconds
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        ResponseEntity<Optional<UserDTO>> responseEntity = userRepository.retrieveUser(authenticationRequest.getUsername());
        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            Optional<UserDTO> optUser = responseEntity.getBody();
            UserDTO user = null;
            if (optUser.isPresent())
                user = optUser.get();
            if (user.getUsername().equals(authenticationRequest.getUsername()) &&
                    user.getPassword().equals(authenticationRequest.getPasswordHash()))
                return ResponseEntity.status(HttpStatus.OK)
                        .body(AuthenticationResponse.builder()
                                .accessToken(generateToken(user.getId(),user.getRole()))
                                .username(user.getUsername())
                                .role(user.getRole())
                                .isAuthenticated(true)
                                .build());
            else
                return ResponseEntity.status(HttpStatus.OK)
                        .body(AuthenticationResponse.builder()
                                .accessToken(null)
                                .username(user.getUsername())
                                .role(user.getRole())
                                .isAuthenticated(false)
                                .build());

        }else{//status code different from 200
            return ResponseEntity.status(responseEntity.getStatusCode()).build();
        }
    }


    public AuthorizationResponse authorize(AuthorizationRequest authorizationRequest) {
        String token = authorizationRequest.getAccessToken();
        Long id = getIdFromToken(token);
        Role role = getRoleFromToken(token);

        return AuthorizationResponse.builder()
                .isAuthorized(validateToken(authorizationRequest.getAccessToken()))
                .userId(id)
                .role(role)
                .build();
    }

    //Generate token inserting id and role
    public String generateToken(Long id, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static boolean validateToken(String token/*,String username*/) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Long getIdFromToken(String token){
        Number n = (Integer) Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id");

        return n.longValue();

    }

    private Role getRoleFromToken(String token){
        return Role.valueOf((String) Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role"));

    }


    /*private String getUsernameFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("sub")
                .toString();

    }*/





}

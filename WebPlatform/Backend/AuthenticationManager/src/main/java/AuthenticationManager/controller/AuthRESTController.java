package AuthenticationManager.controller;

import AuthenticationManager.dto.AuthenticationRequest;
import AuthenticationManager.dto.AuthenticationResponse;
import AuthenticationManager.dto.AuthorizationRequest;
import AuthenticationManager.dto.AuthorizationResponse;
import AuthenticationManager.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthRESTController {
    @Autowired
    private final AuthService authService;
    @PostMapping(value= "/authenticate",consumes = "application/json", produces ="application/json")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest
    ) {
        log.info("Authenticating the user");
        return authService.authenticate(authenticationRequest);
    }

    @GetMapping(value= "/authorize", produces ="application/json")
    public ResponseEntity<AuthorizationResponse> authorize(
            @RequestParam String accessToken
    ){
        log.info("Authorizing the user...");
        return ResponseEntity.status(HttpStatus.OK).body(authService.authorize(new AuthorizationRequest(accessToken)));
    }

}

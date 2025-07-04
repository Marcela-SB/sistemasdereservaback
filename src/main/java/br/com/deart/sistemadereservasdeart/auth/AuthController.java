package br.com.deart.sistemadereservasdeart.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;
    
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
        @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(service.register(request));
    }
    
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(
        @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request));
    }


}

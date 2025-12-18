package br.com.deart.sistemadereservasdeart.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.deart.sistemadereservasdeart.services.JwtService;
import br.com.deart.sistemadereservasdeart.user.IUserRepository;
import br.com.deart.sistemadereservasdeart.user.RoleModel;
import br.com.deart.sistemadereservasdeart.user.UserModel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthenticationResponse register(RegisterRequest request) {
        var user = UserModel.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .name(request.getName())
                        .registration(request.getRegistration())
                        .password(encoder.encode(request.getPassword()))
                        .role(request.getRole())
                        .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken( user);
        return AuthenticationResponse.builder()
                                        .token(jwtToken)
                                        .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        var user = userRepository.findByUsernameAndActiveTrue(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                                        .token(jwtToken)
                                        .user(user)
                                        .build();
    }
    
}

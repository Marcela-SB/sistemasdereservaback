package br.com.deart.sistemadereservasdeart.auth;

import br.com.deart.sistemadereservasdeart.user.UserModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String token;
    private UserModel user;

}

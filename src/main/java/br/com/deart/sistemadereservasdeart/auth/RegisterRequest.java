package br.com.deart.sistemadereservasdeart.auth;

import br.com.deart.sistemadereservasdeart.user.RoleModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String username;
    private String name;
    private String password;
    private String email;
    private String registration;
    private RoleModel role;

}

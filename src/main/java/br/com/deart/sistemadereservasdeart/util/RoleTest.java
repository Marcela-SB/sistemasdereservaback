package br.com.deart.sistemadereservasdeart.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.deart.sistemadereservasdeart.user.IUserRepository;
import br.com.deart.sistemadereservasdeart.user.UserModel;

@Service
public class RoleTest {
    
    @Autowired
    IUserRepository userRepository;
    
    public static boolean permissionTest(UserModel userModel, String roleToTest){
        if(userModel.getRole().equals(roleToTest)){
            return true;
        }
        if(userModel.getRole().equals("admin")){
            return true;
        }
        if(userModel.getRole().equals("supervisor") && !roleToTest.equals("admin")){
            return true;
        }
        System.out.println(userModel.getRole() + "não é suficiente, você precisar ser " + roleToTest);
        return false;
    }

}

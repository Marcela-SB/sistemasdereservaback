package br.com.deart.sistemadereservasdeart.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.deart.sistemadereservasdeart.util.PutTools;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity createUser(@RequestBody UserModel userModel, HttpServletRequest request){

        //Metodo em desuso

        String[] cargos = {"admin","supervisor","bolsista","usuario"};

        var validRole = false;
        for(String c : cargos){
            if (userModel.getRole().equals(c)) {validRole = true;}
        }
        if(!validRole){
            return ResponseEntity.status(400).body("Cargo indisponivel");
        }

        var cryptPassword = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());

        userModel.setPassword(cryptPassword);

        this.userRepository.save(userModel);
        return ResponseEntity.status(201).body(userModel);

    }

    @GetMapping("/list")
    public ResponseEntity listAllUsers(HttpServletRequest request){

        var users = this.userRepository.findAll();
        return ResponseEntity.status(201).body(users);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity deleteUser(@PathVariable UUID userId, HttpServletRequest request){

        var toDeleteUser = this.userRepository.findById(userId).orElse(null);
        
        if (toDeleteUser == null){
            return ResponseEntity.status(404).body("Usuário não existe.");
        } 

        this.userRepository.delete(toDeleteUser);
        return ResponseEntity.status(202).body(toDeleteUser);

    }

    @PutMapping("/edit/{userId}")
    public ResponseEntity changeUser(@RequestBody UserModel userModel, HttpServletRequest request, @PathVariable UUID userId){

        var userToBeChanged = this.userRepository.findById(userId).orElse(null);

        if(userToBeChanged == null){
            return ResponseEntity.status(404).body("Usuário não existe.");
        }

        // * Aqui encriptamos a Password
        var cryptPassword = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(cryptPassword);

        PutTools.copyNonNullProperties(userModel, userToBeChanged);

        var updatedUser = this.userRepository.save(userToBeChanged);

        return ResponseEntity.status(202).body(updatedUser);

    }

    @PatchMapping("/changepassword/{userId}/{newPassword}")
    public ResponseEntity changeUserPassword(HttpServletRequest request, @PathVariable UUID userId, @PathVariable String newPassword){
        
        var userToBeChanged = this.userRepository.findById(userId).orElse(null);

        if(userToBeChanged == null){
            return ResponseEntity.status(404).body("Usuário não existe.");
        }

        // * Aqui encriptamos a Password
        var cryptPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());
        var newPasswordUser = new UserModel();
        newPasswordUser.setPassword(cryptPassword);

        PutTools.copyNonNullProperties(newPasswordUser, userToBeChanged);

        var updatedUser = this.userRepository.save(userToBeChanged);

        return ResponseEntity.status(202).body(updatedUser);

    }

}

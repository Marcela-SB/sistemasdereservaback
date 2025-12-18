package br.com.deart.sistemadereservasdeart.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface IUserRepository extends JpaRepository<UserModel, UUID>{
    //UserModel findByUsername(String username);

    @Query("select u from tb_users u where u.id = ?1")
    UserModel findById(String id);

    List<UserModel> findByActiveTrue();

    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findByUsernameAndActiveTrue(String username);

    //UserModel findByUsernameAndPassword(String username, String password);

    Optional<UserModel> findByEmail(String email);
}

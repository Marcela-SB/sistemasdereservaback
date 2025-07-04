package br.com.deart.sistemadereservasdeart.entregadechave;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IEntregaDeChave extends JpaRepository<EntregaDeChaveModel,UUID>{

} 

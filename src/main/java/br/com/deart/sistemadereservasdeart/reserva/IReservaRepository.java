package br.com.deart.sistemadereservasdeart.reserva;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IReservaRepository extends JpaRepository<ReservaModel,UUID>{
    
}

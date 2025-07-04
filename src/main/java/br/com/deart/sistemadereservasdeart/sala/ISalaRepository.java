package br.com.deart.sistemadereservasdeart.sala;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ISalaRepository extends JpaRepository<SalaModel, UUID>{
    
}

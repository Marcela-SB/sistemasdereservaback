package br.com.deart.sistemadereservasdeart.reserva;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IReservaRepository extends JpaRepository<ReservaModel,UUID>{

    @Query("SELECT r FROM tb_reserva r WHERE r.reservationStart <= :end AND r.reservationEnd >= :start")
    List<ReservaModel> findConflictingDateRange(
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end
    );
    
}

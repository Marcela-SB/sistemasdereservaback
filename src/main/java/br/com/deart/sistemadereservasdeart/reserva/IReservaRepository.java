package br.com.deart.sistemadereservasdeart.reserva;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

public interface IReservaRepository extends JpaRepository<ReservaModel,UUID>{

    @Query("SELECT r FROM tb_reserva r WHERE r.reservationStart <= :endDate AND r.reservationEnd >= :startDate")
    List<ReservaModel> findConflictingDateRange(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT r FROM tb_reserva r WHERE r.reservationStart <= :endDate AND r.reservationEnd >= :startDate AND r.id <> :reservationId")
    List<ReservaModel> findConflictingDateRangeExcludingCurrent(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate, 
        @Param("reservationId") UUID reservationId
    );
    
}

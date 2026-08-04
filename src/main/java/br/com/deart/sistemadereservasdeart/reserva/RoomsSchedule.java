package br.com.deart.sistemadereservasdeart.reserva;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

@Data
@Entity(name = "rooms_schedule")
public class RoomsSchedule {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private ReservaModel reservation;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(length = 300000)
    private UUID[] roomsId;
    
    @Column(length = 65555)
    private Boolean[][] schedule;
}

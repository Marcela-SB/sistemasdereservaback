package br.com.deart.sistemadereservasdeart.excecao;

import br.com.deart.sistemadereservasdeart.enums.Courses;
import br.com.deart.sistemadereservasdeart.reserva.ReservaModel;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.UUID;

@CrossOrigin
@Data
@Entity(name = "tb_excecao")
public class ExcecaoModel {
    @Id
    @GeneratedValue(generator="UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "reserva_id")
    private ReservaModel reserva;


    @Column(length = 300000)
    private UUID[] roomsId;

    private UUID reservationResponsibleId;
    private UUID reservatedToId;

    private Integer slots;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column(length = 65555)
    private Boolean[][] schedule;

    private LocalDateTime excecaoStart;
    private LocalDateTime excecaoEnd;

    @Nullable
    private String comment;

    private Courses course;
}

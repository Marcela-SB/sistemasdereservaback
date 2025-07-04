package br.com.deart.sistemadereservasdeart.reserva;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.deart.sistemadereservasdeart.enums.Courses;
import br.com.deart.sistemadereservasdeart.excecao.ExcecaoModel;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
@Entity(name ="tb_reserva")
public class ReservaModel {

    @Id
    @GeneratedValue(generator="UUID")
    private UUID id;

    @Column(length = 300000)
    private UUID[] roomsId;

    private UUID reservationResponsibleId;
    private UUID reservatedToId;
    private String name;

    private Integer slots;

    @CreationTimestamp
    private LocalDateTime creationDate;
    
    @Column(length = 65555)
    private Boolean[][] schedule;
    
    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;

    @Nullable
    private String comment;

    private Courses course;

    @Nullable
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExcecaoModel> excecoes;
}

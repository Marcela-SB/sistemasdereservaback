package br.com.deart.sistemadereservasdeart.autorizacoes;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.UUID;

@CrossOrigin
@Data
@Entity(name="tb_autorizacoes")
public class AutorizacoesModel {
    @Id
    @GeneratedValue(generator="UUID")
    private UUID id;

    @Column(length = 36 * 100)
    private UUID[] roomsId;

    private String name;

    private UUID authorizationProfessorId;
    private UUID authorizationResponsibleId;

    @Column(length = 36 * 100)
    private UUID[] authorizatedToId;

    @CreationTimestamp
    private LocalDateTime creationDate;

    private LocalDateTime authorizationStart;
    private LocalDateTime authorizationEnd;

    @Nullable
    @Column(columnDefinition = "TEXT")
    private String comment;
}

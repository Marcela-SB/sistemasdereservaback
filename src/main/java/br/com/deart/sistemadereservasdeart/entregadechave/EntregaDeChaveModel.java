package br.com.deart.sistemadereservasdeart.entregadechave;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@CrossOrigin
@Data
@Entity(name = "tb_entregadechave")
public class EntregaDeChaveModel {
    
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private UUID roomId;
    private UUID withdrawResponsibleId;
    private UUID responsibleForTheKeyId;
    private LocalDateTime withdrawTime;

    @Nullable
    private LocalDateTime returnPrevision;

    @Nullable
    private LocalDateTime returnTime;
    
    private Boolean isKeyReturned;

    @Nullable
    private UUID keyReturnedById;


}

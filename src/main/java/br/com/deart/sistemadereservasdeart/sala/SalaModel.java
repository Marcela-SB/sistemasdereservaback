package br.com.deart.sistemadereservasdeart.sala;

import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_sala")
public class SalaModel {
    
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String name;

    @Nullable
    private String roomNumber;
    
    private Integer capacity;
    private Integer chairQuantity;
    private Integer computerQuantity;
    private boolean airConditioning;
    private boolean airConditioningWorking;
    private boolean projector;
    private boolean projectorWorking;
    private boolean bigTables;
    private boolean sinks;
    private boolean hasKey;
    private boolean reservable;
    private boolean administrative;

    @Nullable
    private String comment;
    
    public void setCapacity(Integer capacidade) throws Exception{
        if(capacidade < 0){
            throw new Exception("Capacidade não pode ser negativa");
        }
        this.capacity = capacidade;
    }

    public void setChairQuantity(Integer ndeCadeiras) throws Exception{
        if(ndeCadeiras < 0){
            throw new Exception("Numero de cadeiras não pode ser negativo");
        }
        this.chairQuantity = ndeCadeiras;
    }

    public void setComputerQuantity(Integer ndeComputadores) throws Exception{
        if(ndeComputadores < 0){
            throw new Exception("Numero de computadores não pode ser negativo");
        }
        this.computerQuantity = ndeComputadores;
    }

}

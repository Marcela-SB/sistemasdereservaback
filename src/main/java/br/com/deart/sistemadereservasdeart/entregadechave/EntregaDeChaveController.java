package br.com.deart.sistemadereservasdeart.entregadechave;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.deart.sistemadereservasdeart.sala.ISalaRepository;
import br.com.deart.sistemadereservasdeart.user.IUserRepository;
import br.com.deart.sistemadereservasdeart.util.PutTools;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/keydelivery")
public class EntregaDeChaveController {
    
    @Autowired
    private IEntregaDeChave entregaDeChaveRepository;

    @Autowired
    private ISalaRepository salaRepository;

    @Autowired
    private IUserRepository userRepository;

    @GetMapping("/list")
    public ResponseEntity listAllReservations(HttpServletRequest request){


        var entregas = entregaDeChaveRepository.findAll();
        return ResponseEntity.status(201).body(entregas);
    }

    @PostMapping("/create")
    public ResponseEntity createReservation(HttpServletRequest request, @RequestBody EntregaDeChaveModel entregaDeChaveModel){

        //todo: esses testes se repetem no create e edit, minimizar isso
        var roomId = this.salaRepository.findById(entregaDeChaveModel.getRoomId()).orElse(null);
        if(roomId == null){
            return ResponseEntity.status(404).body("Sala não existe.");
        }

        var deliveryResponsible = this.userRepository.findById(entregaDeChaveModel.getWithdrawResponsibleId()).orElse(null);
        if(deliveryResponsible == null){
            return ResponseEntity.status(404).body("Usuario não existe.");
        }

        var keyResponsible = this.userRepository.findById(entregaDeChaveModel.getResponsibleForTheKeyId()).orElse(null);
        if(keyResponsible == null){
            return ResponseEntity.status(404).body("Usuario não existe.");
        }

        if(entregaDeChaveModel.getWithdrawTime().isAfter(entregaDeChaveModel.getWithdrawTime())){
            return ResponseEntity.status(400).body("Horario da entrega da chave deve vir antes do horario de retirada");
        }

        if(entregaDeChaveModel.getIsKeyReturned() && entregaDeChaveModel.getReturnTime() == null){
            return ResponseEntity.status(400).body("A chave não pode ser entregue sem um horario de devolução");
        }

        if(entregaDeChaveModel.getIsKeyReturned() && entregaDeChaveModel.getKeyReturnedById() == null){
            return ResponseEntity.status(400).body("A chave não pode ser entregue por ninguém");
        }
        //todo: ---

        this.entregaDeChaveRepository.save(entregaDeChaveModel);
        return ResponseEntity.status(201).body(entregaDeChaveModel);
    }

    @DeleteMapping("/delete/{keyDeliveryId}")
    public ResponseEntity deleteKeyDelivery(@PathVariable UUID keyDeliveryId, HttpServletRequest request){

        var keyDeliveryToDelete = this.entregaDeChaveRepository.findById(keyDeliveryId).orElse(null);

        if (keyDeliveryToDelete == null){
            return ResponseEntity.status(400).body("Entrega de chave não existe.");
        } 

        this.entregaDeChaveRepository.delete(keyDeliveryToDelete);
        return ResponseEntity.status(202).body(keyDeliveryToDelete);
    }

    @PutMapping("/edit/{keyDeliveryId}")
    public ResponseEntity editRoom(HttpServletRequest request, @RequestBody EntregaDeChaveModel entregaDeChaveModel, @PathVariable UUID keyDeliveryId){


        var keyDeliveryToChange = this.entregaDeChaveRepository.findById(keyDeliveryId).orElse(null);

        if (keyDeliveryToChange == null){
            return ResponseEntity.status(400).body("Entrega de chave não existe.");
        } 

        PutTools.copyNonNullProperties(entregaDeChaveModel, keyDeliveryToChange);

        //todo: esses testes se repetem no create e edit, minimizar isso
        var roomId = this.salaRepository.findById(keyDeliveryToChange.getRoomId()).orElse(null);
        if(roomId == null){
            return ResponseEntity.status(404).body("Sala não existe.");
        }

        var deliveryResponsible = this.userRepository.findById(keyDeliveryToChange.getWithdrawResponsibleId()).orElse(null);
        if(deliveryResponsible == null){
            return ResponseEntity.status(404).body("Usuario não existe.");
        }

        var keyResponsible = this.userRepository.findById(keyDeliveryToChange.getResponsibleForTheKeyId()).orElse(null);
        if(keyResponsible == null){
            return ResponseEntity.status(404).body("Usuario não existe.");
        }

        if(keyDeliveryToChange.getWithdrawTime().isAfter(keyDeliveryToChange.getWithdrawTime())){
            return ResponseEntity.status(400).body("Horario da entrega da chave deve vir antes do horario de retirada");
        }

        if(keyDeliveryToChange.getIsKeyReturned() && keyDeliveryToChange.getReturnTime() == null){
            return ResponseEntity.status(400).body("A chave não pode ser entregue sem um horario de devolução");
        }

        if(keyDeliveryToChange.getIsKeyReturned() && keyDeliveryToChange.getKeyReturnedById() == null){
            return ResponseEntity.status(400).body("A chave não pode ser entregue por ninguém");
        }
        //todo: ---


        var updatedKeyDelivery = this.entregaDeChaveRepository.save(keyDeliveryToChange);

        return ResponseEntity.status(202).body(updatedKeyDelivery);
    }
}

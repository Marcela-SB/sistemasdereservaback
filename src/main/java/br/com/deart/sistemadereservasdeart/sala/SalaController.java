package br.com.deart.sistemadereservasdeart.sala;

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

import br.com.deart.sistemadereservasdeart.util.PutTools;
import jakarta.servlet.http.HttpServletRequest;


@CrossOrigin
@RestController
@RequestMapping("/room")
public class SalaController {
    
    @Autowired
    private ISalaRepository salaRepository;

    @GetMapping("/list")
    public ResponseEntity listAllRooms(){
        var rooms = this.salaRepository.findAll();
        return ResponseEntity.status(201).body(rooms);
    }

    @PostMapping("/create")
    public ResponseEntity createRoom(@RequestBody SalaModel sala, HttpServletRequest request){
        this.salaRepository.save(sala);
        return ResponseEntity.status(201).body(sala);
    }

    @DeleteMapping("/delete/{roomId}")
    public ResponseEntity deleteReservation(@PathVariable UUID roomId, HttpServletRequest request){

        var toDeleteRoom = this.salaRepository.findById(roomId).orElse(null);
        
        if (toDeleteRoom == null){
            return ResponseEntity.status(400).body("Sala não existe.");
        } 

        this.salaRepository.delete(toDeleteRoom);
        return ResponseEntity.status(202).body(toDeleteRoom);

    }

    @PutMapping("/edit/{roomId}")
    public ResponseEntity editRoom(HttpServletRequest request, @RequestBody SalaModel salaModel, @PathVariable UUID roomId){

        var roomToBeChanged = this.salaRepository.findById(roomId).orElse(null);

        if (roomToBeChanged == null){
            return ResponseEntity.status(400).body("Sala não existe.");
        } 

        PutTools.copyNonNullProperties(salaModel, roomToBeChanged);

        var updatedRoom = this.salaRepository.save(roomToBeChanged);

        return ResponseEntity.status(202).body(updatedRoom);
    }
}

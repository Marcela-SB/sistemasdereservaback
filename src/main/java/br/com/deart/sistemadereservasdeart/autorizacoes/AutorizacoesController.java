package br.com.deart.sistemadereservasdeart.autorizacoes;

import br.com.deart.sistemadereservasdeart.util.PutTools;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/authorization")
public class AutorizacoesController {

    @Autowired
    private IAutorizacoesRepository autorizacoesRepository;

    @GetMapping("/list")
    public ResponseEntity listAllReservations(HttpServletRequest request) {
        var autorizacoes = autorizacoesRepository.findAll();
        return ResponseEntity.status(201).body(autorizacoes);
    }

    @PostMapping("/create")
    public ResponseEntity createRoom(@RequestBody AutorizacoesModel autorizacao, HttpServletRequest request){
        this.autorizacoesRepository.save(autorizacao);
        return ResponseEntity.status(201).body(autorizacao);
    }

    @DeleteMapping("/delete/{autorizationId}")
    public ResponseEntity deleteReservation(@PathVariable UUID autorizationId, HttpServletRequest request){

        var toDeleteAutorization = this.autorizacoesRepository.findById(autorizationId).orElse(null);

        if (toDeleteAutorization == null){
            return ResponseEntity.status(400).body("Autorização não existe.");
        }

        this.autorizacoesRepository.delete(toDeleteAutorization);
        return ResponseEntity.status(202).body(toDeleteAutorization);

    }

    @PutMapping("/edit/{autorizationId}")
    public ResponseEntity editRoom(HttpServletRequest request, @RequestBody AutorizacoesModel autorizacoesModel, @PathVariable UUID autorizationId){

        var roomToBeChanged = this.autorizacoesRepository.findById(autorizationId).orElse(null);

        if (roomToBeChanged == null){
            return ResponseEntity.status(400).body("Autorização não existe.");
        }

        PutTools.copyNonNullProperties(autorizacoesModel, roomToBeChanged);

        var updatedRoom = this.autorizacoesRepository.save(roomToBeChanged);

        return ResponseEntity.status(202).body(updatedRoom);
    }

}

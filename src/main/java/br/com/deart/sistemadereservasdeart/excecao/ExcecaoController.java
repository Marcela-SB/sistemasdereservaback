package br.com.deart.sistemadereservasdeart.excecao;

import br.com.deart.sistemadereservasdeart.reserva.IReservaRepository;
import br.com.deart.sistemadereservasdeart.reserva.ReservaModel;
import br.com.deart.sistemadereservasdeart.util.PutTools;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/excecao")
public class ExcecaoController {

    @Autowired
    private IExcecaoRepository excecaoRepository;

    @Autowired
    private IReservaRepository reservaRepository;

    @GetMapping("/list")
    public ResponseEntity listAllReservations(HttpServletRequest request) {

        var reservas = excecaoRepository.findAll();
        return ResponseEntity.status(201).body(reservas);
    }

    @PostMapping("/create/{reservaId}")
    public ResponseEntity createRoom(@PathVariable UUID reservaId, @RequestBody ExcecaoModel excecao, HttpServletRequest request){
        ReservaModel reserva = this.reservaRepository.findById(reservaId).orElse(null);

        if(reserva == null){
            return  ResponseEntity.status(400).body("Reserva não existente");
        }

        reserva.getExcecoes().add(excecao);
        this.reservaRepository.save(reserva);

        return ResponseEntity.status(201).body(excecao);
    }

    @DeleteMapping("/delete/{excecaoId}")
    public ResponseEntity deleteReservation(@PathVariable UUID excecaoId, HttpServletRequest request){

        var toDeleteExcecao = this.excecaoRepository.findById(excecaoId).orElse(null);

        if (toDeleteExcecao == null){
            return ResponseEntity.status(400).body("Exceção não existe.");
        }

        ReservaModel reserva = toDeleteExcecao.getReserva();
        reserva.getExcecoes().remove(excecaoId);

        this.reservaRepository.save(reserva);
        return ResponseEntity.status(202).body(toDeleteExcecao);

    }

    @PutMapping("/edit/{excecaoId}")
    public ResponseEntity editRoom(HttpServletRequest request, @RequestBody ExcecaoModel excecaoModel, @PathVariable UUID excecaoId){

        var roomToBeChanged = this.excecaoRepository.findById(excecaoId).orElse(null);

        if (roomToBeChanged == null){
            return ResponseEntity.status(400).body("Exceção não existe.");
        }

        PutTools.copyNonNullProperties(excecaoModel, roomToBeChanged);

        var updatedRoom = this.excecaoRepository.save(roomToBeChanged);

        return ResponseEntity.status(202).body(updatedRoom);
    }
    

}

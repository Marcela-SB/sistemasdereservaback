package br.com.deart.sistemadereservasdeart.reserva;

import java.util.List;
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
@RequestMapping("/reservation")
public class ReservaController {

    @Autowired
    private IReservaRepository reservaRepository;

    @Autowired
    private ReservaService service;

    @GetMapping("/list")
    public ResponseEntity listAllReservations(HttpServletRequest request) {

        var reservas = reservaRepository.findAll();
        return ResponseEntity.status(201).body(reservas);
    }

    @PostMapping("/create")
    public ResponseEntity createReservation(@RequestBody ReservaModel reservaModel, HttpServletRequest request) {

        return service.reservationCheck(reservaModel);
    }

    @DeleteMapping("/delete/{reservationId}")
    public ResponseEntity deleteReservation(@PathVariable UUID reservationId, HttpServletRequest request) {

        var toDeleteReservation = this.reservaRepository.findById(reservationId).orElse(null);

        if (toDeleteReservation == null) {
            return ResponseEntity.status(404).body("Reserva não existe.");
        }

        this.reservaRepository.delete(toDeleteReservation);
        return ResponseEntity.status(202).body(toDeleteReservation);

    }

@PutMapping("/edit/{reservationId}")
    public ResponseEntity editRoom(HttpServletRequest request, @RequestBody ReservaModel reservaModel,
            @PathVariable UUID reservationId) {

        var reservationToBeChanged = this.reservaRepository.findById(reservationId).orElse(null);

        if (reservationToBeChanged == null) {
            return ResponseEntity.status(404).body("Reserva não existe.");
        }

        // 1. Atualização explícita campo a campo (evita falhas do PutTools)
        if (reservaModel.getName() != null) {
            reservationToBeChanged.setName(reservaModel.getName());
        }
        if (reservaModel.getCourse() != null) {
            reservationToBeChanged.setCourse(reservaModel.getCourse());
        }
        if (reservaModel.getReservationStart() != null) {
            reservationToBeChanged.setReservationStart(reservaModel.getReservationStart());
        }
        if (reservaModel.getReservationEnd() != null) {
            reservationToBeChanged.setReservationEnd(reservaModel.getReservationEnd());
        }
        if (reservaModel.getComment() != null) {
            reservationToBeChanged.setComment(reservaModel.getComment());
        }
        if (reservaModel.getSlots() != null) {
            reservationToBeChanged.setSlots(reservaModel.getSlots());
        }
        if (reservaModel.getReservatedToId() != null) {
            reservationToBeChanged.setReservatedToId(reservaModel.getReservatedToId());
        }
        if (reservaModel.getReservationResponsibleId() != null) {
            reservationToBeChanged.setReservationResponsibleId(reservaModel.getReservationResponsibleId());
        }

        // 2. Atualização segura e explícita da lista de schedules
        if (reservaModel.getSchedules() != null && !reservaModel.getSchedules().isEmpty()) {
            reservationToBeChanged.getSchedules().clear();
            
            for (RoomsSchedule novoSchedule : reservaModel.getSchedules()) {
                RoomsSchedule scheduleToPersist = new RoomsSchedule();
                scheduleToPersist.setRoomsId(novoSchedule.getRoomsId());
                scheduleToPersist.setSchedule(novoSchedule.getSchedule());

                // Usa o helper method para garantir o vínculo bidirecional correto
                reservationToBeChanged.addRoomsSchedule(scheduleToPersist);
            }
        } else {
            return ResponseEntity.status(400).body("A lista de salas e horários não pode estar vazia.");
        }

        // 3. Executa as validações e salva
        return service.reservationCheck(reservationToBeChanged);
    }
}

package br.com.deart.sistemadereservasdeart.reserva;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.deart.sistemadereservasdeart.sala.ISalaRepository;
import br.com.deart.sistemadereservasdeart.user.IUserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ISalaRepository salaRepository;
    private final IReservaRepository reservaRepository;
    private final IUserRepository userRepository;

    public ResponseEntity reservationCheck(ReservaModel reservaModel) {

        for( UUID reservationRoomId : reservaModel.getRoomsId() ) {
            var roomId = this.salaRepository.findById(reservationRoomId).orElse(null);
            if (roomId == null) {
                return ResponseEntity.status(404).body("Sala não existe.");
            }
        }

        var responsibleUserId = this.userRepository.findById(reservaModel.getReservationResponsibleId()).orElse(null);
        if (responsibleUserId == null) {
            return ResponseEntity.status(404).body("Usuário responsavel pela sala não existe.");
        }

        var reservationToId = this.userRepository.findById(reservaModel.getReservatedToId()).orElse(null);
        if (reservationToId == null) {
            return ResponseEntity.status(404).body("Usuário para quem a sala esta sendo reservada não existe.");
        }

        if (reservaModel.getSchedule() == null) {
            return ResponseEntity.status(400).body("Formato de horarios incorreto");
        }

        if (reservaModel.getReservationStart().isAfter(reservaModel.getReservationEnd())) {
            return ResponseEntity.status(400).body("Data de inicio deve vir antes da data de termino");
        }

        // Aqui conferimos se a reserva entra em conflito de horario com alguma outra
        var allReservations = reservaRepository.findAll();
        for (var reservation : allReservations) {
            for( UUID allReservationsRoomId : reservation.getRoomsId() ) {
                for( UUID reservationRoomId : reservaModel.getRoomsId() ) {
                    // Fazemos a checagem apenas nas reservas que utilizam de uma mesma sala
                    if (allReservationsRoomId.equals(reservationRoomId)) {
                        // Então verificamos se as reservas se sobrepoem em suas datas
                        if (((reservation.getReservationStart().isBefore(reservaModel.getReservationEnd())
                                && reservation.getReservationStart().isAfter(reservaModel.getReservationStart()))
                                || (reservation.getReservationEnd().isBefore(reservaModel.getReservationEnd())
                                && reservation.getReservationEnd().isAfter(reservaModel.getReservationStart())))
                                || ((reservaModel.getReservationStart().isBefore(reservation.getReservationEnd())
                                && reservaModel.getReservationStart().isAfter(reservation.getReservationStart()))
                                || (reservaModel.getReservationEnd().isBefore(reservation.getReservationEnd())
                                && reservaModel.getReservationEnd()
                                .isAfter(reservation.getReservationStart())))) {
                            // Por ultimo checamos se elas conflitam nos horarios especificos das datas em
                            // que se conflitam

                            var weekDays = List.of(0, 1, 2, 3, 4, 5, 6);
                            
                            var reqHasSunday = reservaModel.getSchedule().length > 6;
                            var baseHasSunday =  reservation.getSchedule().length > 6;

                            // Nem req nem base tem domingo
                            if(!reqHasSunday && !baseHasSunday){
                                weekDays = List.of(0, 1, 2, 3, 4, 5);
                            }

                            var toChangeVector = 0;

                            // Apenas 1 dos 2 tem domingo
                            if(reservaModel.getSchedule().length != reservation.getSchedule().length){
                                // Req tem domingo e base não
                                if(reservaModel.getSchedule().length > reservation.getSchedule().length){
                                    toChangeVector = -1;
                                } else {
                                    toChangeVector = 1;
                                }
                            }
                            for (var weekDay : weekDays) {
                                Boolean[] newReservationSchedule = reservaModel.getSchedule()[weekDay];

                                Boolean[] defaultSchedule = new Boolean[16];

                                Boolean[] testReservationSchedule;
                                var trueBaseWeekDay = weekDay + toChangeVector;
                                if(trueBaseWeekDay < 0) {
                                    testReservationSchedule = defaultSchedule;
                                } else {
                                    testReservationSchedule = reservation.getSchedule()[weekDay + toChangeVector];
                                }

                                // Garante que o array default não tenha elementos null
                                for (int i = 0; i < defaultSchedule.length; i++) {
                                    if (defaultSchedule[i] == null) {
                                        defaultSchedule[i] = false;
                                    }
                                }
                                // Garante que testReservationSchedule não é null
                                if (testReservationSchedule == null) {
                                    testReservationSchedule = defaultSchedule;
                                }

                                // O Loop de Verificação Corrigido
                                var hourly = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
                                for (var hour : hourly) {
                                    // Apenas verifica o conflito se AMBOS os horários não forem nulos (e forem true).
                                    // O operador && curto-circuita, impedindo o NPE.

                                    Boolean novoHorario = newReservationSchedule[hour];
                                    Boolean existenteHorario = testReservationSchedule[hour];

                                    // Se ambos são TRUE, há conflito.
                                    // Usamos '!= null' antes de usar 'booleanValue()' ou o autounboxing.
                                    if (novoHorario != null && novoHorario.booleanValue() &&
                                        existenteHorario != null && existenteHorario.booleanValue()) {
                                        
                                        // Em um caso mais simples, se você sabe que os arrays só têm TRUE/FALSE:
                                        // if (Boolean.TRUE.equals(novoHorario) && Boolean.TRUE.equals(existenteHorario)) {
                                        
                                        return ResponseEntity.status(400).body(
                                            "Espaço esta reservado neste horario para a materia " + reservation.getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.reservaRepository.save(reservaModel);
        return ResponseEntity.status(201).body(reservaModel);
    }

}

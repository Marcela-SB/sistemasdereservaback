package br.com.deart.sistemadereservasdeart.reserva;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.deart.sistemadereservasdeart.enums.ClassTime;
import br.com.deart.sistemadereservasdeart.enums.WeekDays;
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

        // 1. Validações iniciais
        if (reservaModel.getSchedules() == null || reservaModel.getSchedules().isEmpty()) {
            return ResponseEntity.status(400).body("A lista de salas e horários não pode estar vazia.");
        }

        for (RoomsSchedule roomSchedule : reservaModel.getSchedules()) {
            if (roomSchedule.getRoomsId() == null) {
                return ResponseEntity.status(400).body("Lista de IDs de salas incorreta.");
            }

            for (UUID reservationRoomId : roomSchedule.getRoomsId()) {
                var roomId = this.salaRepository.findById(reservationRoomId).orElse(null);
                if (roomId == null) {
                    return ResponseEntity.status(404).body("Sala não existe.");
                }
            }

            if (roomSchedule.getSchedule() == null) {
                return ResponseEntity.status(400).body("Formato de horários incorreto");
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

        if (reservaModel.getReservationStart().isAfter(reservaModel.getReservationEnd())) {
            return ResponseEntity.status(400).body("Data de inicio deve vir antes da data de termino");
        }

        // 2. Busca otimizada: traz apenas reservas no mesmo intervalo de datas
        var candidateReservations = reservaRepository.findConflictingDateRange(
            reservaModel.getReservationStart(), 
            reservaModel.getReservationEnd()
        );

        // 3. Verificação de conflito de forma sequencial (thread-safe para o Hibernate)
        Optional<String> conflictMessage = candidateReservations.stream()
            .map(existingReservation -> checkConflictWithReservation(existingReservation, reservaModel))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();

        if (conflictMessage.isPresent()) {
            return ResponseEntity.status(400).body(conflictMessage.get());
        }

        // 4. Associa o relacionamento bidirecional e salva
        if (reservaModel.getSchedules() != null) {
            for (RoomsSchedule schedule : reservaModel.getSchedules()) {
                schedule.setReservation(reservaModel);
            }
        }

        this.reservaRepository.save(reservaModel);
        return ResponseEntity.status(201).body(reservaModel);
    }

    /**
     * Método auxiliar para validar conflitos de uma reserva existente de forma thread-safe.
     */
    private Optional<String> checkConflictWithReservation(ReservaModel existingReservation, ReservaModel newReservation) {
        for (var baseRoomSchedule : existingReservation.getSchedules()) {
            for (var reqRoomSchedule : newReservation.getSchedules()) {
                for (UUID baseRoomsId : baseRoomSchedule.getRoomsId()) {
                    for (UUID reqRoomsId : reqRoomSchedule.getRoomsId()) {
                        
                        if (baseRoomsId.equals(reqRoomsId)) {
                            
                            // Sobreposição de datas
                            if (((existingReservation.getReservationStart().isBefore(newReservation.getReservationEnd())
                                    && existingReservation.getReservationStart().isAfter(newReservation.getReservationStart()))
                                    || (existingReservation.getReservationEnd().isBefore(newReservation.getReservationEnd())
                                    && existingReservation.getReservationEnd().isAfter(newReservation.getReservationStart())))
                                    || ((newReservation.getReservationStart().isBefore(existingReservation.getReservationEnd())
                                    && newReservation.getReservationStart().isAfter(existingReservation.getReservationStart()))
                                    || (newReservation.getReservationEnd().isBefore(existingReservation.getReservationEnd())
                                    && newReservation.getReservationEnd().isAfter(existingReservation.getReservationStart())))) {
                                
                                var weekDays = List.of(0, 1, 2, 3, 4, 5, 6);
                                var reqHasSunday = reqRoomSchedule.getSchedule().length > 6;
                                var baseHasSunday = baseRoomSchedule.getSchedule().length > 6;

                                if (!reqHasSunday && !baseHasSunday) {
                                    weekDays = List.of(0, 1, 2, 3, 4, 5);
                                }

                                var toChangeVector = 0;
                                if (reqRoomSchedule.getSchedule().length != baseRoomSchedule.getSchedule().length) {
                                    if (reqRoomSchedule.getSchedule().length > baseRoomSchedule.getSchedule().length) {
                                        toChangeVector = -1;
                                    } else {
                                        toChangeVector = 1;
                                    }
                                }

                                for (var weekDay : weekDays) {
                                    Boolean[] newReservationSchedule = reqRoomSchedule.getSchedule()[weekDay];
                                    Boolean[] defaultSchedule = new Boolean[16];
                                    Boolean[] testReservationSchedule;
                                    
                                    var trueBaseWeekDay = weekDay + toChangeVector;
                                    if (trueBaseWeekDay < 0 || trueBaseWeekDay >= baseRoomSchedule.getSchedule().length) {
                                        testReservationSchedule = defaultSchedule;
                                    } else {
                                        testReservationSchedule = baseRoomSchedule.getSchedule()[trueBaseWeekDay];
                                    }

                                    for (int i = 0; i < defaultSchedule.length; i++) {
                                        if (defaultSchedule[i] == null) {
                                            defaultSchedule[i] = false;
                                        }
                                    }
                                    if (testReservationSchedule == null) {
                                        testReservationSchedule = defaultSchedule;
                                    }

                                    var hourly = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
                                    for (var hour : hourly) {
                                        Boolean novoHorario = (weekDay < reqRoomSchedule.getSchedule().length && hour < reqRoomSchedule.getSchedule()[weekDay].length) 
                                            ? reqRoomSchedule.getSchedule()[weekDay][hour] : false;
                                            
                                        Boolean existenteHorario = (trueBaseWeekDay >= 0 && trueBaseWeekDay < baseRoomSchedule.getSchedule().length && hour < testReservationSchedule.length) 
                                            ? testReservationSchedule[hour] : false;

                                        if (Boolean.TRUE.equals(novoHorario) && Boolean.TRUE.equals(existenteHorario)) {
                                            ClassTime horarioConflito = ClassTime.fromIndex(hour);
                                            WeekDays diaConflito = WeekDays.fromIndex(weekDay);

                                            String nomeDia = (diaConflito != null) ? diaConflito.getName() : "Desconhecido";
                                            String horarioFormatado = (horarioConflito != null) ? horarioConflito.getDescription() : "Desconhecido";

                                            String mensagem = String.format(
                                                "Espaço já reservado para '%s' no dia %s, horário %s.",
                                                existingReservation.getName(), 
                                                nomeDia, 
                                                horarioFormatado
                                            );
                                            
                                            return Optional.of(mensagem);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }
}
package com.zenejarat.backend.controller;

import com.zenejarat.backend.model.Reservation;
import com.zenejarat.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Ezzel jelzem, hogy ez egy REST vezérlőosztály, ami HTTP kérésekre válaszol.
@RequestMapping("/api/reservations") // Minden végpontom az /api/reservations útvonal alá fog tartozni.
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService; // Konstruktoron keresztül megkapom a foglaláskezelő szolgáltatást.
    }

    @GetMapping // Lekérem az összes foglalást.
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations(); // Meghívom a szolgáltatást, hogy adja vissza az összes foglalást.
    }

    @GetMapping("/{id}") // Lekérek egy adott foglalást azonosító alapján.
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id); // Megpróbálom lekérni a foglalást.
        return reservation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Ha nincs, 404-et adok vissza.
    }

    @PostMapping // Létrehozok egy új foglalást.
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        Reservation savedReservation = reservationService.saveReservation(reservation); // Elmentem az új foglalást.
        return ResponseEntity.ok(savedReservation); // Visszaküldöm a mentett objektumot.
    }

    @PutMapping("/{id}") // Frissítem egy meglévő foglalást ID alapján.
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservationDetails) {
        Optional<Reservation> updatedReservation = reservationService.getReservationById(id)
                .map(existingReservation -> {
                    // Beállítom az új értékeket a meglévő foglalásban.
                    existingReservation.setStartTime(reservationDetails.getStartTime());
                    existingReservation.setEndTime(reservationDetails.getEndTime());
                    existingReservation.setVenue(reservationDetails.getVenue());
                    return reservationService.saveReservation(existingReservation); // Elmentem a frissített adatokat.
                });
        return updatedReservation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Ha nem találom, 404-et adok.
    }

    @DeleteMapping("/{id}") // Törlök egy foglalást az ID alapján.
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id); // Meghívom a szolgáltatást a törléshez.
        return ResponseEntity.noContent().build(); // 204 No Content választ adok, ha sikeres volt.
    }
}

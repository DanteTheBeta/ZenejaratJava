package com.zenejarat.backend.controller;

import com.zenejarat.backend.model.Reservation;
import com.zenejarat.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Jogosultságkezelés annotáció
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Ezzel jelzem, hogy ez egy REST vezérlőosztály, ami HTTP kérésekre válaszol.
@RequestMapping("/api/reservations") // Minden végpont az /api/reservations útvonal alá tartozik.
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        // Konstruktoron keresztül megkapom a foglaláskezelő szolgáltatást.
        this.reservationService = reservationService;
    }

    //  Csak admin láthatja az összes foglalást
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Reservation> getAllReservations() {
        // Meghívom a szolgáltatást, hogy visszaadja az összes foglalást
        return reservationService.getAllReservations();
    }

    //  Csak admin kérhet le egy adott foglalást ID alapján
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        // Megpróbálom lekérni a foglalást az ID alapján
        Optional<Reservation> reservation = reservationService.getReservationById(id);

        // Ha megtaláltam, visszaadom 200 OK-kal, különben 404-et küldök
        return reservation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //  Csak bejelentkezett felhasználó hozhat létre foglalást
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        // Elmentem az új foglalást a szolgáltatáson keresztül
        Reservation savedReservation = reservationService.saveReservation(reservation);

        // Visszaküldöm a mentett objektumot 200 OK válasszal
        return ResponseEntity.ok(savedReservation);
    }

    //  Csak admin frissíthet foglalást
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservationDetails) {
        // Megpróbálom lekérni az eredeti foglalást ID alapján
        Optional<Reservation> updatedReservation = reservationService.getReservationById(id)
                .map(existingReservation -> {
                    // Beállítom az új értékeket
                    existingReservation.setStartTime(reservationDetails.getStartTime());
                    existingReservation.setEndTime(reservationDetails.getEndTime());
                    existingReservation.setVenue(reservationDetails.getVenue());

                    // Elmentem az új értékeket és visszaadom
                    return reservationService.saveReservation(existingReservation);
                });

        // Ha sikerült frissíteni, visszaadom, ha nem, 404-et küldök
        return updatedReservation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //  Csak admin törölhet foglalást
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        // Meghívom a szolgáltatást a törléshez
        reservationService.deleteReservation(id);

        // 204 No Content válasszal jelzem, hogy sikeres volt
        return ResponseEntity.noContent().build();
    }
}

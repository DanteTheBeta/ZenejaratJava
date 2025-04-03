package com.zenejarat.backend.controller;

import com.zenejarat.backend.model.Ticket;
import com.zenejarat.backend.service.TicketService;
import com.zenejarat.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Ezzel jelzem, hogy ez egy REST típusú kontroller, amely HTTP kérésekre válaszol.
@RequestMapping("/api/tickets") // Az összes jegyhez tartozó végpont az /api/tickets útvonal alá kerül.
public class TicketController {

    private final TicketService ticketService;
    private final EmailService emailService;

    @Autowired
    public TicketController(TicketService ticketService, EmailService emailService) {
        // Konstruktoron keresztül kapom meg a jegykezelő és email szolgáltatásokat.
        this.ticketService = ticketService;
        this.emailService = emailService;
    }

    @GetMapping // Lekérem az összes jegyet.
    public List<Ticket> getAllTickets() {
        // Meghívom a service réteget, hogy visszakapjam az összes jegyet.
        return ticketService.getAllTickets();
    }

    @GetMapping("/{id}") // Lekérek egy adott jegyet ID alapján.
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        // Megpróbálom lekérni a jegyet az ID alapján.
        Optional<Ticket> ticket = ticketService.getTicketById(id);

        // Ha megtaláltam, visszaküldöm, ha nem, 404-et adok vissza.
        return ticket.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping // Létrehozok egy új jegyet és küldök visszaigazoló emailt.
    public ResponseEntity<?> createTicket(@RequestBody Ticket ticket) {
        // Lekérem az eseményt, amelyhez a jegyet vásárolják.
        var event = ticket.getEvent();

        // Megnézem, van-e még elérhető hely az eseményen.
        if (event.getAvailableSeats() <= 0) {
            // Ha nincs több jegy, hibát küldök vissza.
            return ResponseEntity.badRequest().body(" Nincs több elérhető jegy erre az eseményre.");
        }

        // Elmentem a jegyet, ha van még hely.
        Ticket savedTicket = ticketService.saveTicket(ticket);

        // Csökkentem a szabad férőhelyek számát az eseményen.
        event.setAvailableSeats(event.getAvailableSeats() - 1);

        // Frissítem az eseményt az új jegyszámmal.
        ticketService.updateEventAfterTicketPurchase(event); // ezt a metódust a service-ben valósítom meg

        // Összeállítom a visszaigazoló email tartalmát.
        String subject = "Jegyvásárlás visszaigazolása";
        String text = "Kedves " + savedTicket.getUser().getUsername() + ",\n\n" +
                "Köszönjük a jegyvásárlást! Az esemény: " + savedTicket.getEvent().getName() +
                " sikeresen lefoglalt.\n\nÜdvözlettel,\nZenejárat csapata";

        // Elküldöm az emailt a felhasználónak.
        emailService.sendConfirmationEmail(savedTicket.getUser().getEmail(), subject, text);

        // Visszaküldöm a mentett jegyet válaszként.
        return ResponseEntity.ok(savedTicket);
    }

    @PutMapping("/{id}") // Frissítem egy meglévő jegyet azonosító alapján.
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody Ticket ticketDetails) {
        // Megpróbálom lekérni a meglévő jegyet.
        Optional<Ticket> updatedTicket = ticketService.getTicketById(id).map(existingTicket -> {
            // Beállítom az új értékeket.
            existingTicket.setStatus(ticketDetails.getStatus());
            existingTicket.setPrice(ticketDetails.getPrice());
            existingTicket.setEvent(ticketDetails.getEvent());
            existingTicket.setUser(ticketDetails.getUser());

            // Elmentem a frissített jegyet.
            return ticketService.saveTicket(existingTicket);
        });

        // Visszatérek az eredménnyel vagy 404 hibával.
        return updatedTicket.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") // Törlök egy jegyet ID alapján.
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        // Meghívom a törlő szolgáltatást.
        ticketService.deleteTicket(id);

        // Visszatérek 204 No Content válasszal.
        return ResponseEntity.noContent().build();
    }
}

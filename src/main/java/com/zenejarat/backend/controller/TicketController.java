package com.zenejarat.backend.controller;

import com.zenejarat.backend.model.Ticket;
import com.zenejarat.backend.service.TicketService;
import com.zenejarat.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController // Ez a vezérlő REST API végpontokat kezel jegyekhez.
@RequestMapping("/api/tickets") // Minden végpont az /api/tickets útvonal alá tartozik.
public class TicketController {

    private final TicketService ticketService;
    private final EmailService emailService;

    @Autowired
    public TicketController(TicketService ticketService, EmailService emailService) {
        this.ticketService = ticketService; // A jegyek kezelését végző szolgáltatást itt kapom meg.
        this.emailService = emailService;   // Az email küldéséhez szükséges szolgáltatást itt injektálom.
    }

    @GetMapping // Lekérem az összes jegyet.
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets(); // A szolgáltatáson keresztül visszakérem az összes jegyet.
    }

    @GetMapping("/{id}") // Egy adott jegyet kérek le ID alapján.
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Optional<Ticket> ticket = ticketService.getTicketById(id); // Megpróbálom lekérni a jegyet az adatbázisból.
        return ticket.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Ha nincs ilyen jegy, 404-et adok vissza.
    }

    @PostMapping // Létrehozok egy új jegyet, és visszaigazoló emailt küldök róla.
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        Ticket savedTicket = ticketService.saveTicket(ticket); // Elmentem a jegyet.

        // Összeállítom a visszaigazoló email tartalmát.
        String subject = "Jegyvásárlás visszaigazolása";
        String text = "Kedves " + savedTicket.getUser().getUsername() + ",\n\n" +
                "Köszönjük a jegyvásárlást! Az esemény: " + savedTicket.getEvent().getName() +
                " sikeresen lefoglalt.\n\nÜdvözlettel,\nZenejárat csapata";

        // Elküldöm az emailt a felhasználónak.
        emailService.sendConfirmationEmail(savedTicket.getUser().getEmail(), subject, text);

        return ResponseEntity.ok(savedTicket); // Visszaküldöm a mentett jegyet.
    }

    @PutMapping("/{id}") // Frissítem egy meglévő jegy adatait ID alapján.
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody Ticket ticketDetails) {
        Optional<Ticket> updatedTicket = ticketService.getTicketById(id).map(existingTicket -> {
            // Frissítem a mezőket a beérkezett adatok alapján.
            existingTicket.setStatus(ticketDetails.getStatus());
            existingTicket.setPrice(ticketDetails.getPrice());
            existingTicket.setEvent(ticketDetails.getEvent());
            existingTicket.setUser(ticketDetails.getUser());
            return ticketService.saveTicket(existingTicket); // Elmentem a frissített jegyet.
        });
        return updatedTicket.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Ha nem található a jegy, 404-et adok.
    }

    @DeleteMapping("/{id}") // Törlöm a jegyet azonosító alapján.
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id); // Meghívom a törlő metódust.
        return ResponseEntity.noContent().build(); // 204-es státuszkódot küldök vissza, ami üres választ jelent.
    }
}

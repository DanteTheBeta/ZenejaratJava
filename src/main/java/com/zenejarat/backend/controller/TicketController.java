package com.zenejarat.backend.controller;

import com.zenejarat.backend.model.Ticket;
import com.zenejarat.backend.service.TicketService;
import com.zenejarat.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final EmailService emailService;

    @Autowired
    public TicketController(TicketService ticketService, EmailService emailService) {
        this.ticketService = ticketService;
        this.emailService = emailService;
    }

    // Minden jegy lekérdezése
    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    // Egy jegy lekérdezése ID alapján
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Optional<Ticket> ticket = ticketService.getTicketById(id);
        return ticket.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Új jegy létrehozása visszaigazoló email küldésével
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        Ticket savedTicket = ticketService.saveTicket(ticket);
        // Küldj visszaigazoló emailt a felhasználónak
        String subject = "Jegyvásárlás visszaigazolása";
        String text = "Kedves " + savedTicket.getUser().getUsername() + ",\n\n" +
                "Köszönjük a jegyvásárlást! Az esemény: " + savedTicket.getEvent().getName() +
                " sikeresen lefoglalt.\n\nÜdvözlettel,\nZenejárat csapata";
        emailService.sendConfirmationEmail(savedTicket.getUser().getEmail(), subject, text);
        return ResponseEntity.ok(savedTicket);
    }

    // Jegy frissítése
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody Ticket ticketDetails) {
        Optional<Ticket> updatedTicket = ticketService.getTicketById(id).map(existingTicket -> {
            existingTicket.setStatus(ticketDetails.getStatus());
            existingTicket.setPrice(ticketDetails.getPrice());
            existingTicket.setEvent(ticketDetails.getEvent());
            existingTicket.setUser(ticketDetails.getUser());
            return ticketService.saveTicket(existingTicket);
        });
        return updatedTicket.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Jegy törlése
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}

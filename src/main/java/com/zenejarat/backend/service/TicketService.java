package com.zenejarat.backend.service;

import com.zenejarat.backend.model.Ticket;
import com.zenejarat.backend.model.Event;
import com.zenejarat.backend.repository.TicketRepository;
import com.zenejarat.backend.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    private EventRepository eventRepository; // 🔹 Események frissítéséhez

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    //  Ezt hívja a TicketController
    public void updateEventAfterTicketPurchase(Event event) {
        eventRepository.save(event);
    }
}

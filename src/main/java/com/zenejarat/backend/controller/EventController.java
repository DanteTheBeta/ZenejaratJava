package com.zenejarat.backend.controller;

import com.zenejarat.backend.model.Event;
import com.zenejarat.backend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Ezzel jelzem, hogy ez egy REST típusú vezérlőosztály.
@RequestMapping("/api/events") // Az összes végpont az /api/events útvonal alá fog tartozni.
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService; // Konstruktoron keresztül kapom meg az eseményekhez tartozó szolgáltatást.
    }

    @GetMapping // Lekérem az összes eseményt.
    public List<Event> getAllEvents() {
        return eventService.getAllEvents(); // Az összes eseményt visszaadom a szolgáltatásból.
    }

    @GetMapping("/{id}") // Lekérem az eseményt azonosító alapján.
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id); // Megpróbálom lekérni az eseményt.
        return event.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Ha nincs ilyen esemény, 404-et adok vissza.
    }

    @PostMapping // Létrehozok egy új eseményt.
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event savedEvent = eventService.saveEvent(event); // Elmentem az eseményt.
        return ResponseEntity.ok(savedEvent); // Visszaküldöm a mentett eseményt.
    }

    @PutMapping("/{id}") // Frissítem egy meglévő esemény adatait azonosító alapján.
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        Optional<Event> updatedEvent = eventService.getEventById(id)
                .map(existingEvent -> {
                    // Frissítem az esemény mezőit az új értékekkel.
                    existingEvent.setName(eventDetails.getName());
                    existingEvent.setDescription(eventDetails.getDescription());
                    existingEvent.setEventDate(eventDetails.getEventDate());
                    existingEvent.setVenue(eventDetails.getVenue());
                    return eventService.saveEvent(existingEvent); // Elmentem a frissített eseményt.
                });
        return updatedEvent.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Ha nem találom az eseményt, 404-et adok.
    }

    @DeleteMapping("/{id}") // Törlöm az eseményt azonosító alapján.
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id); // Meghívom a szolgáltatást, hogy törölje az eseményt.
        return ResponseEntity.noContent().build(); // Üres választ küldök vissza, ami a sikeres törlés jele.
    }
}

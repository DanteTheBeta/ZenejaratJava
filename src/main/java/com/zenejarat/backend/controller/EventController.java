package com.zenejarat.backend.controller;

import com.zenejarat.backend.model.Event;
import com.zenejarat.backend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; //  Ezzel tudom védeni a végpontokat
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Ezzel jelzem, hogy ez egy REST típusú vezérlőosztály.
@RequestMapping("/api/events") // Az összes végpont az /api/events útvonal alá fog tartozik.
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        // Konstruktoron keresztül kapom meg az EventService példányt
        this.eventService = eventService;
    }

    @GetMapping // Lekérem az összes eseményt. Ez publikus, nincs korlátozva.
    public List<Event> getAllEvents() {
        // Meghívom a service réteget, és visszaadom az összes eseményt
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}") // Lekérek egy konkrét eseményt azonosító alapján.
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        // Megpróbálom lekérni az eseményt az id alapján
        Optional<Event> event = eventService.getEventById(id);

        // Ha megtaláltam, visszaadom 200 OK státusszal, ha nem, 404 NOT FOUND-ot küldök
        return event.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔐 Csak ADMIN szerepkörű felhasználók hozhatnak létre eseményt
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        // Meghívom a service réteget, hogy mentse el az eseményt
        Event savedEvent = eventService.saveEvent(event);

        // Visszaadom a mentett eseményt 200 OK válaszként
        return ResponseEntity.ok(savedEvent);
    }

    // 🔐 Csak ADMIN frissíthet eseményt
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        // Megpróbálom lekérni az eseményt, amit frissíteni szeretnék
        Optional<Event> updatedEvent = eventService.getEventById(id)
                .map(existingEvent -> {
                    // Itt frissítem a meglévő esemény mezőit az új adatokkal
                    existingEvent.setName(eventDetails.getName());
                    existingEvent.setDescription(eventDetails.getDescription());
                    existingEvent.setEventDate(eventDetails.getEventDate());
                    existingEvent.setVenue(eventDetails.getVenue());

                    // Mentem az új adatokat és visszatérek az eredménnyel
                    return eventService.saveEvent(existingEvent);
                });

        // Ha sikerült frissíteni, visszatérek az új eseménnyel, ha nem találtam, 404-et küldök
        return updatedEvent.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //  Csak ADMIN törölhet eseményt
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        // Meghívom a service-t, hogy törölje az eseményt
        eventService.deleteEvent(id);

        // Visszatérek üres (204 No Content) válasszal
        return ResponseEntity.noContent().build();
    }
}

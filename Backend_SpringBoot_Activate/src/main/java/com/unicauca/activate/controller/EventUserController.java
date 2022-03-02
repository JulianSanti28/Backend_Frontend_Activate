package com.unicauca.activate.controller;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.unicauca.activate.model.Asistence;
import com.unicauca.activate.model.Event;
import com.unicauca.activate.model.EventUser;

import java.util.ArrayList;
import java.util.List;
import com.unicauca.activate.model.User;
import com.unicauca.activate.model.UserEventKey;
import com.unicauca.activate.service.EventService;
import com.unicauca.activate.service.IEventUserService;
import com.unicauca.activate.service.IUserService;
import com.unicauca.activate.utilities.JWTUtilities;

import org.hibernate.annotations.SourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activate/assist")

public class EventUserController {
    
    @Autowired
    private IEventUserService EventUserService;

    @Autowired
    private IUserService UserService;

    @Autowired
    private EventService EventService;

    @Autowired
    private JWTUtilities jwUtil;
    //Crear Asistencia
    @PostMapping("create")
    public ResponseEntity<?> create(@RequestBody Asistence asistence) {
        System.out.println(asistence.toString());
        //Long usuarioID = Long.parseLong(jwUtil.getKey(token)); 
        //Long eventoID = Long.parseLong(event_id); 
        //Asistence asistence = new Asistence(usuarioID, eventoID);
        Optional<User> user = UserService.findById(asistence.getIdentificacionUsuario());
        Optional<Event> event = EventService.findById(asistence.getIdentificacionEvento());
        //Relación N:M
        EventUser eventUser = new EventUser();
        
        //Guardar la relación N:M 
        
        UserEventKey userEventKey = new UserEventKey();

        userEventKey.setEventId(asistence.getIdentificacionEvento());
        userEventKey.setUserId(asistence.getIdentificacionUsuario());

        eventUser.setId(userEventKey);
        eventUser.setEvent(event.get());
        eventUser.setUser(user.get());

        event.get().addAsistence(eventUser);
        user.get().addAsistence(eventUser);

        EventUser save = EventUserService.save(eventUser);
        System.out.println("Pasoooooo1");
        return ResponseEntity.ok().body(save);
    }

    @GetMapping("eventsUser/all/{id}")
    public List<Event> readAllEvents(@PathVariable Long id) {
        System.out.println(id+"--------------------------------");
        Optional<User> user = UserService.findById(id);
        Set<EventUser> eventsUser = user.get().getAssistences();
        List<Event> events = new ArrayList<>();
        for (EventUser temp : eventsUser) {
            events.add(temp.getEvent());
        }
        return events;
    }

}

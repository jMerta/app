package io.mapovent.app.transport.rest.event;

import com.mongodb.MongoException;
import io.mapovent.app.domain.event.entity.Event;
import io.mapovent.app.domain.event.service.EventService;
import io.mapovent.app.domain.event.validator.EventValidator;
import io.mapovent.app.domain.helper.FilterElement;
import io.mapovent.app.domain.helper.entity.ValidationException;
import io.mapovent.app.transport.rest.CrudController;
import io.mapovent.app.transport.rest.GenericController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/event")
public class EventController extends GenericController implements CrudController<Event> {

  private final EventService eventService;
  private final EventValidator eventValidator;

  public EventController(EventService eventService, EventValidator eventValidator) {
    this.eventService = eventService;
    this.eventValidator = eventValidator;
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<Event> find(@PathVariable("id") String id) {
    Optional<Event> event = eventService.find(id);
    return event.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(()
      -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping()
  public ResponseEntity<List<Event>> find(@RequestParam Optional<List<FilterElement>> filterBy) {
    return new ResponseEntity<>(eventService.find(filterBy), HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<String> create(@RequestBody Event entity) throws ValidationException {
      validate(entity, eventValidator);
      return new ResponseEntity<>(eventService.create(entity), HttpStatus.CREATED);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<Event> update(@PathVariable("id") String id, @RequestBody Event entity) {
    try {
      return new ResponseEntity<>(eventService.update(id, entity), HttpStatus.OK);
    } catch (MongoException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity delete(@PathVariable String id) {
    eventService.delete(id);
    return new ResponseEntity(HttpStatus.OK);
  }

  @GetMapping(value = "/findByUser")
  public ResponseEntity<List<Event>> findByUser() {
    return new ResponseEntity<>(eventService.findByUser(getCurrentUser()), HttpStatus.OK);
  }
}

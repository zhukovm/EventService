package org.eventservice.hibernate.reactive.api;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eventservice.hibernate.reactive.entities.Event;
import org.eventservice.hibernate.reactive.entities.UUIDContainer;
import org.eventservice.hibernate.reactive.service.EventsService;

import java.util.List;

import static org.jboss.resteasy.reactive.RestResponse.StatusCode.CREATED;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.OK;

@Path("events")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class EventsApi {
    //todo
    // make events creation
    // make subscription
    // make notifications (on event creation(for category's subscription), on event modification)
    // check email sent
    @Inject
    EventsService eventsService;


    @POST
    @Path("registrationCheck")
    public Uni<Response> registrationCheck(UUIDContainer eventDescription) {
        eventDescription.getUuid();
        return eventsService.registrationCheck(eventDescription)
                .replaceWith(Response.ok().build());
    }

    @GET
    public Uni<List<Event>> get() {
        return eventsService.listEvents();
    }

    @GET
    @Path("{id}")
    public Uni<Event> get(String id) {
        return eventsService.getEvent(id);
    }

    @POST
    public Uni<Response> createEvent(Event event) {
        return eventsService.createEvent(event)
                .replaceWith(Response.ok(event).status(CREATED)::build);
    }

    @PUT
    public Uni<Response> modifyEvent(Event event) {
        return eventsService.modifyEvent(event)
                .replaceWith(Response.ok(event).status(OK)::build);
    }
}

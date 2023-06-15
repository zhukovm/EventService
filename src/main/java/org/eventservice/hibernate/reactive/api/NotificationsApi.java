package org.eventservice.hibernate.reactive.api;

import io.smallrye.mutiny.Uni;
import org.eventservice.hibernate.reactive.entities.Notification;
import org.eventservice.hibernate.reactive.service.NotificationsService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import java.util.List;

@Path("notifications")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class NotificationsApi {
    @Inject
    NotificationsService notificationsService;

    @GET
    public Uni<List<Notification>> list() {
        return notificationsService.list();
    }
}

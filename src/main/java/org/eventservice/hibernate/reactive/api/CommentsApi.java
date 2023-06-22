package org.eventservice.hibernate.reactive.api;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eventservice.hibernate.reactive.entities.Comment;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("comments")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class CommentsApi {
    @Inject
    Mutiny.SessionFactory sf;

    @GET
    public Uni<List<Comment>> listByEvent(@QueryParam("event.uuid") String eventUuid) {
        return sf.withSession(s -> s
                .createNamedQuery("Comments.findAllByEventUuid", Comment.class)
                .setParameter("eventUuid", eventUuid)
                .getResultList()
        );
    }

    @POST
    public Uni<Response> createComment(Comment comment) {
        return sf.withTransaction((s, t) -> s.persist(comment))
                .replaceWith(Response.ok(comment).status(CREATED)::build);
    }
}

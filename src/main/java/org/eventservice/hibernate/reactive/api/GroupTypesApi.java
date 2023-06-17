package org.eventservice.hibernate.reactive.api;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eventservice.hibernate.reactive.entities.GroupType;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("grouptype")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class GroupTypesApi {
    @Inject
    Mutiny.SessionFactory sf;

    @GET
    public Uni<List<GroupType>> list() {
        return sf.withSession(s -> s
                .createNamedQuery("GroupTypes.findAll", GroupType.class)
                .getResultList()
        );
    }

    @POST
    public Uni<Response> createGroupType(GroupType groupType) {
        return sf.withTransaction((s, t) -> s.persist(groupType))
                .replaceWith(Response.ok(groupType).status(CREATED)::build);
    }
}

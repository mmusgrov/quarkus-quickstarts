package org.acme.rest.json;

import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.eclipse.microprofile.lra.annotation.ws.rs.LRA.LRA_HTTP_CONTEXT_HEADER;
import static org.eclipse.microprofile.lra.annotation.ws.rs.LRA.LRA_HTTP_ENDED_CONTEXT_HEADER;

@Path("fruits")
public class FruitResource {
    private static final Logger LOGGER = Logger.getLogger(FruitResource.class.getName());
    private Set<Fruit> fruits = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));
    static AtomicInteger completions = new AtomicInteger(0);
    static AtomicInteger compensations = new AtomicInteger(0);

    public FruitResource() {
        fruits.add(new Fruit("Apple", "Winter fruit"));
        fruits.add(new Fruit("Pineapple", "Tropical fruit"));
    }

    @GET
    @Path("report")
    public String report() {
        System.out.printf("XXXX listing fruits%n");
        return String.format("%d completions and %d compensations%n",
                compensations.get(), compensations.get());
    }

    @POST
    @Path("tx")
    @LRA
    public String doInTx(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) URI lraId) {
        return lraId.toASCIIString();
    }

    @PUT
    @Path("compensate")
    @Compensate
    public Response compensateWork(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) URI lraId, String userData) {
        if (lraId == null) {
            throw new NullPointerException("lraId can't be null as it should be invoked with the context");
        }

        compensations.incrementAndGet();
        LOGGER.warning(String.format("LRA id '%s' was told to compensate", lraId));

        return Response.ok(lraId.toASCIIString()).build();
    }

    @PUT
    @Path("complete")
    @Complete
    public Response completeWork(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) URI lraId, String userData) {
        if (lraId == null) {
            throw new NullPointerException("lraId can't be null as it should be invoked with the context");
        }

        completions.incrementAndGet();
        LOGGER.warning(String.format("LRA id '%s' was told to complete", lraId));

        return Response.ok(lraId.toASCIIString()).build();
    }
}

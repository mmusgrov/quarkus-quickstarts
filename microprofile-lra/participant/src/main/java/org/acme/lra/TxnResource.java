package org.acme.lra;

import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static org.eclipse.microprofile.lra.annotation.ws.rs.LRA.LRA_HTTP_CONTEXT_HEADER;

@Path("txns")
public class TxnResource {
    private static final Logger LOGGER = Logger.getLogger(TxnResource.class.getName());
    static AtomicInteger completions = new AtomicInteger(0);
    static AtomicInteger compensations = new AtomicInteger(0);


    @GET
    @Path("report")
    public String report() {
        return String.format("%d completions and %d compensations%n",
                completions.get(), compensations.get());
    }

    @POST
    @Path("close")
    @LRA
    public String doInTx(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) URI lraId) {
        return lraId.toASCIIString();
    }

    @POST
    @Path("cancel")
    @LRA
    public String cancelInTx(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) URI lraId) {
        return Response.status(Status.BAD_REQUEST).entity(lraId).build();
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

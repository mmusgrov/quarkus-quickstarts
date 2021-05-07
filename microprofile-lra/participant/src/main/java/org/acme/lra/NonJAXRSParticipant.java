/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2020, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.acme.lra;

import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ParticipantStatus;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.eclipse.microprofile.lra.annotation.ws.rs.LRA.LRA_HTTP_CONTEXT_HEADER;
import static org.eclipse.microprofile.lra.annotation.ws.rs.LRA.LRA_HTTP_RECOVERY_HEADER;

@ApplicationScoped
@Path(NonJAXRSParticipant.SIMPLE_PARTICIPANT_RESOURCE_PATH)
public class NonJAXRSParticipant {
    public static final String SIMPLE_PARTICIPANT_RESOURCE_PATH = "non-jaxrs-participant";
    public static final String START_LRA_PATH = "start-lra";
    public static final String RESET_ACCEPTED_PATH = "reset-accepted";

    private static final AtomicBoolean accepted = new AtomicBoolean(false);

    @GET
    @Path(START_LRA_PATH)
    @LRA(value = LRA.Type.REQUIRED)
    public Response doInLRA(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) URI lraId, @HeaderParam(LRA_HTTP_RECOVERY_HEADER) URI recoveryUrl) {
        accepted.set(true);

        return Response.status(Response.Status.OK)
                .header(LRA_HTTP_RECOVERY_HEADER, recoveryUrl.toASCIIString())
                .entity(lraId.toASCIIString())
                .build();
    }

    @GET
    @Path("test")
    public Response reset() {
        accepted.set(false);

        return Response.ok("").build(); // return some entity since one of the tests expects it
    }

    @Compensate
    public ParticipantStatus compensate(URI lraId) {
        return ParticipantStatus.Compensated;
    }

    @Complete
    public ParticipantStatus complete(URI lraId) {
        return ParticipantStatus.Completed;
    }
}

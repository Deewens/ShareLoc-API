package shareloc.security;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.User;


@Provider
@SignInNeeded
public class JWTAuthFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null)
            throw new NotAuthorizedException("Bearer");

        // récupération du JWT et vérification de la signature
        if (authHeader.startsWith("Bearer")) {
            try {
                // test de validation de la signature et décodage du contenu du token
                final String subject = validate(authHeader.split(" ")[1]);

                // définition du contexte de sécurité
                final SecurityContext securityContext = requestContext.getSecurityContext();
                if (subject != null) {
                    requestContext.setSecurityContext(new SecurityContext() {
                        @Override
                        public Principal getUserPrincipal() {
                            return new Principal() {
                                @Override
                                public String getName() {
                                    return subject;
                                }
                            };
                        }

                        @Override
                        public boolean isUserInRole(String role) {
                            // List<String> roles = AuthRessource.findUserRoles(subject); -> pour que ça ne provoque l'erreur
                            List<String> roles = null;
                            if (roles != null)
                                return roles.contains(role);
                            return false;
                        }

                        @Override
                        public boolean isSecure() {
                            return securityContext.isSecure();
                        }

                        @Override
                        public String getAuthenticationScheme() {
                            return securityContext.getAuthenticationScheme();
                        }
                    });
                }
            } catch (InvalidJwtException ex) {
                requestContext.setProperty("auth-failed", true);
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }

        } else {
            requestContext.setProperty("auth-failed", true);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private String validate(String jwt) throws InvalidJwtException {
        String subject = null;
        RsaJsonWebKey rsaJsonWebKey = RsaKeyProducer.produce();

        // construction du décodeur de JWT
        JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireSubject()
                .setVerificationKey(rsaJsonWebKey.getKey())
                .build();

        // validation du JWT et récupération du contenu
        JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
        subject = (String) jwtClaims.getClaimValue("sub");

        return subject;
    }
}

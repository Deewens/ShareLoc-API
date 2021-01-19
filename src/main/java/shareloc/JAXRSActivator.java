package shareloc;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import shareloc.resources.AuthRessource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@ApplicationPath("/api/*")
public class JAXRSActivator extends ResourceConfig {
    public JAXRSActivator() {
        packages("shareloc");
        register(MultiPartFeature.class);
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

    }

    /*@Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> resources = new HashSet<>();

        resources.add(MultiPartFeature.class);
        return resources;
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        props.put("jersey.config.server.provider.packages", "shareloc");
        return props;
    }*/
}

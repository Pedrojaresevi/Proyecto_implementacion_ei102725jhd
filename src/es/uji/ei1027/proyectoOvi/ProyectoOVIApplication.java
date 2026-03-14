package es.uji.ei1027.proyectoOvi;

import java.util.logging.Logger;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class ProyectoOVIApplication {
    private static final Logger log =
            Logger.getLogger(ProyectoOVIApplication.class.getName());

    public static void main(String[] args) {
        // Auto-configura l'aplicació
        new SpringApplicationBuilder(ProyectoOVIApplication.class).run(args);
    }
}

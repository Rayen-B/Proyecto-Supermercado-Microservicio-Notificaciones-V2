package cl.supermercado.notificaciones.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Notificaciones API")
                        .version("1.0.0")
                        .description("Documentación interactiva de los endpoints de notificaciones"));
    }

    @Bean
    public GroupedOpenApi categoriesApi() {
        return GroupedOpenApi.builder()
                .group("1. Módulo de notificaciones")
                .pathsToMatch("/api/v1/notificaciones/**")
                .build();
    }
}
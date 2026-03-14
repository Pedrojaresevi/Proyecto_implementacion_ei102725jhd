package es.uji.ei1027.proyectoOvi;

import javax.sql.DataSource;

@Configuration
public class ProyectoOVIConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

}

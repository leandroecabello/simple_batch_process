package com.example.demospringbatch.listener;

import com.example.demospringbatch.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/*
Le agregamos el @Component porque más adelante se necesitara inyectar está clase
a un metodo de la configuración de nuestro spring batch
*/
@Component
public class JobListener extends JobExecutionListenerSupport {

    private static final Logger LOG = LoggerFactory.getLogger(JobListener.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JobListener(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Generamos logica para verificar cuando el estado del job finalizo correctamente
        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
            LOG.info("Finalizó el Job!!! Verificar los resultados: ");

            jdbcTemplate.query("SELECT name, lastname, phone FROM person",
                    (rs, row) -> new Person(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3)
                    ))
                    .forEach(person -> LOG.info("Registro " + person));
        }
    }
}

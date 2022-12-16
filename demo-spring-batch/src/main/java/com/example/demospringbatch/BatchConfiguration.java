package com.example.demospringbatch;

import com.example.demospringbatch.listener.JobListener;
import com.example.demospringbatch.model.Person;
import com.example.demospringbatch.processor.PersonItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing // Es necesario habilitar el procesamiento batch
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean //Mediante este bean definimos el reader de csv
    public FlatFileItemReader<Person> reader(){
        /* **
        * FlatFileItemReader es una clase que nos proporciona Spring Batch que está especializada en el tratamiento de ficheros en texto plano.
        * Esta clase es una implementación de la interface ItemReader que define un comportamiento muy sencillo:
        * lee datos de la fuente de datos que sea y los devuélve en un objeto.
        * ** */
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")// nombre del item reader
                .resource(new ClassPathResource("sample-data.csv")) // recurso: archivo con los datos a importar
                .delimited() // delimitado -> por defecto spring lo buscara por coma
                .names(new String[] {"name", "lastname", "phone"}) // nombres de las propiedades, deben estar en el mismo orden de la clase
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>(){{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    public PersonItemProcessor processor(){
        return new PersonItemProcessor();
    }

    @Bean // Mediante este Bean definimos la escritura en base de datos
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource){
        /* **
        * JdbcBatchItemWriter : Utiliza sentencias de tipología PreparedStatement y puede utilizar steps
        * rudimentarios para localizar fallos en la persistencia de la información.
        *  ** */
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO person (name, lastname, phone) VALUES (:name, :lastname, :phone)")
                .dataSource(dataSource)
                .build();
    }

    @Bean // Aquí se define el Job
    public Job importPersonJob(JobListener listener, Step step){
        return jobBuilderFactory.get("importPersonJob")
                .incrementer(new RunIdIncrementer()) // debido a que los jobs generan registros ya sea en memoria o en db, si tenemos varios batch cada uno genera un id diferente por lo que le debemos indicar un incrementer para quw la estructura interna de spring batch funcione correctamente
                .listener(listener)
                .flow(step)// Se podrían incluir múltiples steps
                .end()
                .build();
    }

    @Bean // Aqui se define el step, observamos que se le pasan como parametros reader ,writer y processor
    public Step stepOne(JdbcBatchItemWriter<Person> writer){
        return stepBuilderFactory.get("stepOne")
                .<Person, Person> chunk(10) // le indicamos en cuanto lotes dividir la información , Es posible parameterizar el tamaño del chunk
                .reader(reader())
                .processor(processor()) // opcional
                .writer(writer)
                .build();
    }
}

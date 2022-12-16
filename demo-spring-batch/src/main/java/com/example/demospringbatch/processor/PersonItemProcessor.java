package com.example.demospringbatch.processor;


import com.example.demospringbatch.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public  class PersonItemProcessor implements ItemProcessor<Person, Person> {
//ItemProcessor<Persona, Persona> se le pasa parametros de entrada y salida (en este caso usamos los mismos pueden ser distintos)
    private static final Logger LOG = LoggerFactory.getLogger(PersonItemProcessor.class);

    /* **
    * se define (sobrescribe) un método process que recibirá un elemento (elemento que ha leido nuestro reader)
    * y devolverá los datos procesados en otro elemento
    * Ej: otro POJO (AnotherElement) que contiene uno o varios atributos que representará el elemento o (elementos)
    * que queremos escribir en nuestra salida.
    ** */
    @Override
    public Person process(Person item) throws Exception {
        // tomamos los datos y los pasamos a mayuscula
        String name = item.getName().toUpperCase();
        String lastname = item.getLastname().toUpperCase();
        String phone = item.getPhone();

        Person person = new Person(name, lastname, phone);

        LOG.info("Convirtiendo ("+item+") a ("+person+")");
        return person;
    }
}
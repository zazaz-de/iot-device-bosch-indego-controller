package de.zazaz.iot.bosch.indego.srvmock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IndegoServerMock {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(IndegoServerMock.class, args);
    }

}
package com.jrj.pruebatecnica;

import com.jrj.pruebatecnica.entities.Prendas;
import com.jrj.pruebatecnica.entities.Promociones;
import com.jrj.pruebatecnica.services.PrendasService;
import com.jrj.pruebatecnica.services.PromocionesService;
import com.jrj.pruebatecnica.services.jpa.PromocionesServiceJpaImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PruebatecnicaApplication {

    private static Logger logger = LoggerFactory.getLogger(PruebatecnicaApplication.class);   

    public static void main(String[] args) {
        SpringApplication.run(PruebatecnicaApplication.class, args);
    }


}

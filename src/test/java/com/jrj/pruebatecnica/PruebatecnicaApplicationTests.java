package com.jrj.pruebatecnica;

import com.jrj.pruebatecnica.services.PrendasService;
import com.jrj.pruebatecnica.services.PromocionesService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PruebatecnicaApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class PruebatecnicaApplicationTests {

    static final Logger logger = LoggerFactory.getLogger(PruebatecnicaApplicationTests.class);
    
    @Autowired
    private PrendasService prendasService;
    
    @Autowired
    private PromocionesService promoService;

    @Test
    void contextLoads() {

    }

}

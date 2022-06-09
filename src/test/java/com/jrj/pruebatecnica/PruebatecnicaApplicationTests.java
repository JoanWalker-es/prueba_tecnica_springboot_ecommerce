package com.jrj.pruebatecnica;


import com.jrj.pruebatecnica.entities.Categoria;
import com.jrj.pruebatecnica.entities.Prendas;
import com.jrj.pruebatecnica.entities.Promociones;
import com.jrj.pruebatecnica.services.PrendasService;
import com.jrj.pruebatecnica.services.PromocionesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.*;

@SpringBootTest(classes = PruebatecnicaApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)

class PruebatecnicaApplicationTests {

    static final Logger logger = LoggerFactory.getLogger(PruebatecnicaApplicationTests.class);
    @Autowired
    private PrendasService prendasService;
    @Autowired
    private PromocionesService promoService;
    private Set<Categoria> categorias=new HashSet<>();
    private Prendas prenda=new Prendas("S123456789",new BigDecimal(10.00),new BigDecimal(10.00),categorias);
    private Promociones promo=new Promociones("BLACK",new BigDecimal(10.00));

    @Test
    @Order(0)
    void contextLoads() {
    }
    @Test()
    @Order(1)
    void prendaListIsEmpty() {
        logger.info("Comprobar db vacía");
        List<Prendas> lasPrendas = prendasService.findAll();
        int prendas=lasPrendas.size();
        Assertions.assertEquals(0,prendas);
    }
    @Test
    @Order(2)
    void createPrenda(){
        logger.info("Comprobar creación de prenda en memoria");
        categorias.add(Categoria.Hombre);
        Assertions.assertNotNull(prenda);
    }

    @Test
    @Order(3)
    void savePrendaJPA(){
        logger.info("Comprobar guardado de prenda en JPA");
        Prendas newPrenda=prendasService.add(prenda);
        Assertions.assertNotNull(newPrenda);
    }

    @Test
    @Order(4)
    void deletePrendaJPA(){
        logger.info("Borrado de prenda en JPA");
        Prendas newPrenda=prendasService.add(prenda);
        String ref=newPrenda.getReferencia();
        prendasService.delete(newPrenda.getReferencia());
        Prendas recuperada=prendasService.findByReference(ref);
        Assertions.assertNull(recuperada);
    }

    @Test
    @Order(5)
    void findAllJPA(){
        logger.info("Encontrar todas las prendas desde JPA");
        prendasService.add(prenda);
        prenda.setReferencia("L123456789");
        prendasService.add(prenda);
        prenda.setReferencia("M123456789");
        prendasService.add(prenda);
        int totalPrendas=prendasService.findAll().size();
        Assertions.assertEquals(3,totalPrendas);
    }

    @Test
    @Order(6)
    void findPrendaByRef(){
        logger.info("Encontrar prenda por referencia desde JPA");
        prendasService.add(prenda);
        String ref="S123456789";
        Prendas newPrenda=prendasService.findByReference(ref);
        Assertions.assertEquals(ref,prenda.getReferencia());
    }

    @Test
    @Order(7)
    void createPromo(){
        logger.info("Comprobar creación de promoción JPA");
        Promociones promoJPA=promoService.add(promo);
        Assertions.assertNotNull(promoJPA);
    }
    @Test
    @Order(8)
    void deletePromo(){
        logger.info("Comprobar borrado de promoción JPA");
        Promociones promoJPA=promoService.add(promo);
        promoService.delete(promoJPA.getNombre());
        Promociones promoJPA2=promoService.findByName(promoJPA.getNombre());
        Assertions.assertNull(promoJPA2);
    }

    @Test
    @Order(9)
    void findByPromoByName(){
        logger.info("Encontrar promoción por nombre JPA");
        promoService.add(promo);
        Promociones promoJPA=promoService.findByName(promo.getNombre());
        String nombre="BLACK";
        Assertions.assertEquals(nombre,promoJPA.getNombre());
    }

    @Test
    @Order(10)
    void applyPromo(){
        logger.info("Comprobar que devuelve un map con una prenda y una promoción desde el apply JPA");
        Map<String,Object> prendasConPromo=promoService.apply(promo.getNombre(), prenda.getReferencia());
        Assertions.assertEquals(2,prendasConPromo.size());
        Assertions.assertNotNull(prendasConPromo.get("BLACK"));
        Assertions.assertNotNull(prendasConPromo.get("S123456789"));
    }

    @Test
    @Order(11)
    void unapplyPromo(){
        logger.info("Comprobar que devuelve un map con una prenda y una promoción desde el unapply JPA");
        Map<String,Object> prendasConPromo=promoService.unapply(promo.getNombre(), prenda.getReferencia());
        Assertions.assertEquals(2,prendasConPromo.size());
        Assertions.assertNotNull(prendasConPromo.get("BLACK"));
        Assertions.assertNotNull(prendasConPromo.get("S123456789"));
    }




}

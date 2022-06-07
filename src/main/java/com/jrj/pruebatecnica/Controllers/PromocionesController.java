package com.jrj.pruebatecnica.Controllers;

import com.jrj.pruebatecnica.entities.Prendas;
import com.jrj.pruebatecnica.entities.Promociones;
import com.jrj.pruebatecnica.response.PrendaResponse;
import com.jrj.pruebatecnica.services.PrendasService;
import com.jrj.pruebatecnica.services.PromocionesService;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/promociones")
public class PromocionesController {

    private static final Logger logger = LoggerFactory.getLogger(PromocionesController.class);

    @Autowired
    private PromocionesService promoService;

    @Autowired
    private PrendasService prendaService;

    @PostMapping(value = "")
    public ResponseEntity<Promociones> add(@RequestBody Promociones promo) {
        logger.info("CREANDO PROMO");
        Promociones promos = promoService.add(promo);
        if (promos != null) {
            logger.info("PROMO CREADA " + HttpStatus.CREATED.value());
            logger.info((promos.toString()));
            return ResponseEntity.status(HttpStatus.CREATED).body(promos);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping(value = "/{nombre}")
    public ResponseEntity<?> delete(@PathVariable String nombre) {
        logger.info("BORRANDO PROMO");
        Promociones promo = promoService.findByName(nombre);        
        if (promo != null) {
            List<Prendas> prendas = prendaService.findAll();
            for (Prendas p : prendas) {
                if (p.getPromocionesDePrendas().contains(promo)) {
                    p.getPromocionesDePrendas().remove(promo);
                    logger.info("calculando precio prenda " + p.getReferencia() + " precio: " + p.getPrecio_promocionado());
                    p.setPrecio_promocionado(calculaPromos(p));
                    Prendas newPrenda = prendaService.add(p);
                    logger.info("Precio prenda calculado " + newPrenda.getReferencia() + " precio: " + newPrenda.getPrecio_promocionado());
                }
            }
            promoService.delete(nombre);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new PrendaResponse("PROMOCION ELIMINADA"));

        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new PrendaResponse("PROMOCION NO EXISTE"));
        }
    }

    @PutMapping("/aplicar")
    @Operation(summary = "Aplica una promoción a una prenda")
    public ResponseEntity<?> applyPromo(@RequestParam(name = "promocion") String nombre, @RequestParam(name = "prenda") String referencia) {
        Prendas pren = prendaService.findByReference(referencia);
        if (pren != null) {
            logger.info("Encuentra la prenda");
            Promociones prom = promoService.findByName(nombre);
            if (prom != null) {
                logger.info("Encuentra la promo");
                if (!pren.getPromocionesDePrendas().contains(prom)) {
                    pren.getPromocionesDePrendas().add(prom);
                    logger.info("Añade la promo");
                    double precio = calculaPromos(pren);
                    logger.info("Calcula precio");
                    pren.setPrecio_promocionado(precio);
                    logger.info("Calculado precio promo");
                    prendaService.add(pren);
                    logger.info("Prenda guardada con nuevo precio");
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(pren);
                }
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new PrendaResponse("PROMO YA APLICADA A LA PRENDA"));
            }
            logger.info("PROMOCION NO ENCONTRADA " + HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PrendaResponse("PROMO NO ENCONTRADA"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PrendaResponse("PRENDA NO ENCONTRADA"));
    }

    @PutMapping("/desaplicar")
    @Operation(summary = "Desaplica una promoción a una prenda")
    public ResponseEntity<?> unapplyPromo(@RequestParam(name = "promocion") String nombre, @RequestParam(name = "prenda") String referencia) {
        Prendas pren = prendaService.findByReference(referencia);
        if (pren != null) {
            Promociones prom = promoService.findByName(nombre);
            if (prom != null) {
                pren.getPromocionesDePrendas().remove(prom);
                pren.setPrecio_promocionado(calculaPromos(pren));
                prendaService.add(pren);            
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(pren);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PrendaResponse("PROMO NO ENCONTRADA"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PrendaResponse("PRENDA NO ENCONTRADA"));
    }

    private double calculaPromos(Prendas prenda) {
        double precioPrenda = prenda.getPrecio();
        double resultado = 0;
        for (Promociones promo : prenda.getPromocionesDePrendas()) {          
            resultado = precioPrenda - (precioPrenda * (promo.getDescuento() / 100));
        }
        if (resultado <= 0) {
            resultado = precioPrenda;
        }
        return resultado;
    }

    private double formato(double precio) {
        BigDecimal bd = new BigDecimal(precio);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

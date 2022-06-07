package com.jrj.pruebatecnica.Controllers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jrj.pruebatecnica.entities.Prendas;
import com.jrj.pruebatecnica.entities.Promociones;
import com.jrj.pruebatecnica.response.HttpResponse;
import com.jrj.pruebatecnica.services.PrendasService;
import com.jrj.pruebatecnica.services.PromocionesService;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
            return ResponseEntity.status(HttpStatus.CREATED).body(promos);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @ExceptionHandler({InvalidFormatException.class})
    public ResponseEntity<?> capturExceptionCategoria(InvalidFormatException ex) {
        logger.info("ERROR EN LA RECEPCION DE LA PROMOCION " + HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HttpResponse("DESCUENTO DE LA PROMOCION MAL FORMADO"));
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
                    p.setPrecio_promocionado(calculaPromos(p));
                    Prendas newPrenda = prendaService.add(p);                    
                }
            }
            promoService.delete(nombre);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new HttpResponse("PROMOCION ELIMINADA"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse("PROMOCION NO EXISTE"));
        }
    }

    @PutMapping("/aplicar")
    @Operation(summary = "Aplica una promoción a una prenda")
    public ResponseEntity<?> applyPromo(@RequestParam(name = "promocion") String nombre, @RequestParam(name = "prenda") String referencia) {
        logger.info("APLICANDO PROMO");
        Prendas pren = prendaService.findByReference(referencia);
        if (pren != null) {
            Promociones prom = promoService.findByName(nombre);
            if (prom != null) {                
                if (!pren.getPromocionesDePrendas().contains(prom)) {
                    pren.getPromocionesDePrendas().add(prom);                    
                    double precio = calculaPromos(pren);                    
                    pren.setPrecio_promocionado(precio);                    
                    prendaService.add(pren);                    
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(pren);
                }
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new HttpResponse("PROMO YA APLICADA A LA PRENDA"));
            }
            logger.info("PROMOCION NO ENCONTRADA " + HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpResponse("PROMO NO ENCONTRADA"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpResponse("PRENDA NO ENCONTRADA"));
    }

    @PutMapping("/desaplicar")
    @Operation(summary = "Desaplica una promoción a una prenda")
    public ResponseEntity<?> unapplyPromo(@RequestParam(name = "promocion") String nombre, @RequestParam(name = "prenda") String referencia) {
        logger.info("DESAPLICANDO PROMO");
        Prendas pren = prendaService.findByReference(referencia);
        if (pren != null) {
            Promociones prom = promoService.findByName(nombre);
            if (prom != null) {
                pren.getPromocionesDePrendas().remove(prom);
                pren.setPrecio_promocionado(calculaPromos(pren));
                prendaService.add(pren);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(pren);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpResponse("PROMO NO ENCONTRADA"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpResponse("PRENDA NO ENCONTRADA"));
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
        return formato(resultado);
    }

    private double formato(double precio) {
//        BigDecimal bd = new BigDecimal(precio);
//        bd = bd.setScale(2, RoundingMode.HALF_UP);
//        return bd.doubleValue();
        
        DecimalFormat df = new DecimalFormat("0.00");
        String prec=df.format(precio);
        String precSin=prec.replace(",",".");
        logger.info(precSin);
        double precioFormateado = Double.valueOf(precSin);
        return precioFormateado;
    }
}

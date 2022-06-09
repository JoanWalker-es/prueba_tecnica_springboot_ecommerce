package com.jrj.pruebatecnica.Controllers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jrj.pruebatecnica.entities.Prendas;
import com.jrj.pruebatecnica.entities.Promociones;
import com.jrj.pruebatecnica.response.HttpResponse;
import com.jrj.pruebatecnica.services.PrendasService;
import com.jrj.pruebatecnica.services.PromocionesService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ApiOperation(value = "Crea una nueva promocion",notes = "Crea una nueva promoción con su nombre y descuento.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, response = HttpResponse.class, message = "Promocion creada correctamente"),
        @ApiResponse(code = 400, response = HttpResponse.class, message = "Promocion no creada, datos mal formados")})
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
    public ResponseEntity<?> capturExceptionDescuento(InvalidFormatException ex) {
        logger.info("ERROR EN LA RECEPCION DE LA PROMOCION " + HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HttpResponse("DESCUENTO DE LA PROMOCION MAL FORMADO"));
    }
    
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<?> capturExceptionDescuentoNumero(HttpMessageNotReadableException ex) {
        logger.info("ERROR EN LA RECEPCION DE LA PROMOCION " + HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HttpResponse("DESCUENTO DE LA PROMOCION MAL FORMADO"));
    }

    @ApiOperation(value = "Elimina una promocion",notes = "Elimina una promoción y la desaplica automáticamente a todas las prendas asociadas.")
    @ApiResponses(value = {
        @ApiResponse(code = 204, response = HttpResponse.class, message = "Promocion eliminada"),
        @ApiResponse(code = 404, response = HttpResponse.class, message = "Promocion no encontrada")})
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
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new HttpResponse("PROMOCION ELIMINADA"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpResponse("PROMOCION NO EXISTE"));
        }
    }

    @ApiOperation(value = "Aplica una promocion a una prenda",notes = "Aplica una promoción a una prenda y recalcula el precio promocional de esta.")
    @ApiResponses(value = {
        @ApiResponse(code = 202, response = HttpResponse.class, message = "Promocion aplicada a la prenda"),
        @ApiResponse(code = 404, response = HttpResponse.class, message = "Prenda/promocion no encontrada")})
    @PutMapping("/aplicar")
    public ResponseEntity<?> applyPromo(@RequestParam(name = "promocion") String nombre, @RequestParam(name = "prenda") String referencia) {
        logger.info("APLICANDO PROMO");
        HashMap<String, Object> prendasPromo = promoService.apply(nombre, referencia);
        Optional<Prendas> prenOpt = (Optional) prendasPromo.get(referencia);
        Optional<Promociones> promOpt = (Optional) prendasPromo.get(nombre);

        if (prenOpt.isPresent()) {
            if (promOpt.isPresent()) {
                if (!prenOpt.get().getPromocionesDePrendas().contains(promOpt.get())) {
                    prenOpt.get().getPromocionesDePrendas().add(promOpt.get());
                    BigDecimal precio = calculaPromos(prenOpt.get());
                    prenOpt.get().setPrecio_promocionado(precio);
                    prendaService.add(prenOpt.get());
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(prenOpt.get());
                }
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new HttpResponse("PROMO YA APLICADA A LA PRENDA"));
            }
            logger.info("PROMOCION NO ENCONTRADA " + HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpResponse("PROMO NO ENCONTRADA"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpResponse("PRENDA NO ENCONTRADA"));
    }

    @ApiOperation(value = "Desaplica una promocion a una prenda",notes = "Desaplica una promoción de una prenda y recalcula el precio promocional de la prenda.")
    @ApiResponses(value = {
        @ApiResponse(code = 202, response = HttpResponse.class, message = "Promocion desaplicada a la prenda"),
        @ApiResponse(code = 404, response = HttpResponse.class, message = "Prenda/promocion no encontrada")})
    @PutMapping("/desaplicar")
    @Operation(summary = "Desaplica una promoción a una prenda")
    public ResponseEntity<?> unapplyPromo(@RequestParam(name = "promocion") String nombre, @RequestParam(name = "prenda") String referencia) {
        logger.info("DESAPLICANDO PROMO");
        HashMap<String, Object> prendasPromo = promoService.apply(nombre, referencia);
        Optional<Prendas> prenOpt = (Optional) prendasPromo.get(referencia);
        Optional<Promociones> promOpt = (Optional) prendasPromo.get(nombre);

        if (prenOpt.isPresent()) {
            if (promOpt.isPresent()) {
                prenOpt.get().getPromocionesDePrendas().remove(promOpt.get());
                prenOpt.get().setPrecio_promocionado(calculaPromos(prenOpt.get()));
                prendaService.add(prenOpt.get());
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(prenOpt.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpResponse("PROMO NO ENCONTRADA"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpResponse("PRENDA NO ENCONTRADA"));
    }

    private BigDecimal calculaPromos(Prendas prenda) {
        BigDecimal precioPrendaPromo = prenda.getPrecio();
        BigDecimal resultado=new BigDecimal(0);
        if (prenda.getPromocionesDePrendas().isEmpty()) {
            resultado = precioPrendaPromo;
        } else {
            for (Promociones promo : prenda.getPromocionesDePrendas()) {
                resultado = precioPrendaPromo.subtract((precioPrendaPromo.multiply(promo.getDescuento().divide(new BigDecimal(100)))));
                precioPrendaPromo=resultado;
            }
            if(resultado.compareTo(new BigDecimal(0))<0){
                resultado=new BigDecimal(0);;
            }
        }
        return formato(resultado);
    }

    private BigDecimal formato(BigDecimal precio) {

        precio = precio.setScale(2, RoundingMode.HALF_UP);
        return precio;
    }
}

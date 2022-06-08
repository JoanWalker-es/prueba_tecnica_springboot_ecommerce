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

    @ApiOperation(value = "Crea una nueva promocion")
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
    public ResponseEntity<?> capturExceptionCategoria(InvalidFormatException ex) {
        logger.info("ERROR EN LA RECEPCION DE LA PROMOCION " + HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HttpResponse("DESCUENTO DE LA PROMOCION MAL FORMADO"));
    }

    @ApiOperation(value = "Elimina una promocion")
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
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new HttpResponse("PROMOCION ELIMINADA"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse("PROMOCION NO EXISTE"));
        }
    }

    @ApiOperation(value = "Aplica una promocion a una prenda")
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
                    double precio = calculaPromos(prenOpt.get());
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

    @ApiOperation(value = "Desaplica una promocion a una prenda")
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

    private double calculaPromos(Prendas prenda) {
        double precioPrenda = prenda.getPrecio();
        double resultado = 0;
        if (prenda.getPromocionesDePrendas().isEmpty()) {
            resultado = precioPrenda;
        } else {
            for (Promociones promo : prenda.getPromocionesDePrendas()) {
                resultado = precioPrenda - (precioPrenda * (promo.getDescuento() / 100));
            }
        }
        return formato(resultado);
    }

    private double formato(double precio) {
//        BigDecimal bd = new BigDecimal(precio);
//        bd = bd.setScale(2, RoundingMode.HALF_UP);
//        return bd.doubleValue();

//        DecimalFormat df = new DecimalFormat("0.00");
//        String prec=df.format(precio);
//        String precSin=prec.replace(",",".");
//        logger.info(precSin);
//        double precioFormateado = Double.valueOf(precSin);
//        return precioFormateado;
        
        
        Double format = Math.round(precio*100.0)/100.0;
        return format;


    }
}

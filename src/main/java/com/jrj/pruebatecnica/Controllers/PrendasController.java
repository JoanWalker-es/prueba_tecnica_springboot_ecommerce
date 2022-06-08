package com.jrj.pruebatecnica.Controllers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jrj.pruebatecnica.entities.Prendas;
import com.jrj.pruebatecnica.response.HttpResponse;
import com.jrj.pruebatecnica.services.PrendasService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/prendas")
public class PrendasController {

    private static final Logger logger = LoggerFactory.getLogger(PrendasController.class);

    @Autowired
    private PrendasService prendasService;

    @ApiOperation(value = "Crea una nueva prenda",notes = "Crea una nueva prenda con su referencia, precio y categorias asociadas.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, response = HttpResponse.class, message = "Prenda creada correctamente"),
        @ApiResponse(code = 400, response = HttpResponse.class, message = "Prenda no creada, datos mal formados")})
    @PostMapping(value = "")
    public ResponseEntity<?> add(@RequestBody Prendas prenda) {
        logger.info("CREANDO UNA PRENDA");
        //S,M,L        
        Pattern pat = Pattern.compile("^[S,M,L]{1}[a-zA-Z0-9]{9}");
        Matcher mat = pat.matcher(prenda.getReferencia());
        if (mat.matches()) {
            prenda.setPrecio(prenda.getPrecio());
            prenda.setPrecio_promocionado(prenda.getPrecio());
            Prendas newPrenda = prendasService.add(prenda);
            if (newPrenda != null) {
                logger.info("PRENDA CREADA " + HttpStatus.CREATED.value());
                return ResponseEntity.status(HttpStatus.CREATED).body(newPrenda);
            } else {
                logger.info("PRENDA NO CREADA " + HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HttpResponse("PRENDA NO CREADA"));
            }
        } else {
            logger.info("PRENDA NO CREADA ERROR REFERENCIA " + HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HttpResponse("LA REF DE LA PRENDA DEBE COMENZAR POR S,M,L Y TENER UNA LONGITUD MÁXIMA DE 10 CARACTERES "));
        }
    }

    @ExceptionHandler({InvalidFormatException.class})
    public ResponseEntity<?> capturExceptionCategoria(InvalidFormatException ex) {
        logger.info("ERROR EN LA RECEPCION DE LA CATEGORIA " + HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HttpResponse("EL VALOR DE CATEGORIA NO COINCIDE CON NINGUNO DE LOS ACEPTADOS"));
    }

    @ApiOperation(value = "Elimina una prenda",notes = "Elimina una prenda indicando la referencia.")
    @ApiResponses(value = {
        @ApiResponse(code = 204, response = HttpResponse.class, message = "Prenda eliminada"),
        @ApiResponse(code = 404, response = HttpResponse.class, message = "Prenda no encontrada")})
    @DeleteMapping(value = "/{referencia}")    
    public ResponseEntity<?> delete(@PathVariable String referencia) {
        logger.info("BORRANDO UNA PRENDA");
        Prendas prenda = prendasService.findByReference(referencia);
        if (prenda != null) {
            prendasService.delete(referencia);
            logger.info("PRENDA BORRADA " + HttpStatus.NO_CONTENT.value());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new HttpResponse("PRENDA ELIMINADA CORRECTAMENTE"));
        } else {
            logger.info("ERROR PRENDA NO ENCONTRADA");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpResponse("PRENDA NO ENCONTRADA " + HttpStatus.NOT_FOUND.value()));
        }
    }

    @ApiOperation(value = "Encuentra todas las prendas",notes = "Recupera todas las prendas.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, response = HttpResponse.class, message = "Prendas recuperadas")})
    @GetMapping(value = "")    
    public ResponseEntity<List<Prendas>> findAll() {
        List<Prendas> prendas = prendasService.findAll();
        logger.info(("PRENDAS ENCONTRADAS " + HttpStatus.OK.value()));
        return ResponseEntity.status(HttpStatus.OK).body(prendas);
    }

    @ApiOperation(value = "Recupera una prenda por referencia",notes = "Recupera una prenda indicando la referencia de esta.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, response = HttpResponse.class, message = "Prenda recuperada"),
        @ApiResponse(code = 404, response = HttpResponse.class, message = "Prenda no encontrada")})
    @GetMapping(value = "/{ref}")    
    public ResponseEntity<?> findByReference(@PathVariable String ref) {
        Prendas prenda = prendasService.findByReference(ref);
        if (prenda != null) {
            logger.info("PRENDA ENCONTRADA " + HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(prenda);
        } else {
            logger.info("PRENDA NO ENCONTRADA " + HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpResponse("PRENDA NO ENCONTRADA"));
        }
    }

    private double formato(double precio) {
        BigDecimal bd = new BigDecimal(precio);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}

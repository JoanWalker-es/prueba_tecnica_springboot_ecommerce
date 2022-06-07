package com.jrj.pruebatecnica.Controllers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jrj.pruebatecnica.entities.Prendas;
import com.jrj.pruebatecnica.response.PrendaResponse;
import com.jrj.pruebatecnica.services.PrendasService;
import com.jrj.pruebatecnica.services.PromocionesService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Autowired
    private PromocionesService promoService;

//    @ApiOperation(value = "Crea una nueva prenda", notes = "Devuelve la prenda creada con su precio promocional")
//    @ApiResponses(value = {
//        @ApiResponse(description = "code = 200, message = Created - The product was created"),
//        @ApiResponse(description= "code = 404, message = Not found - The product was not found")
//    })
//    @Operation(summary = "Crea una nueva prenda", description = "Devuelve la prenda creada con su precio promocional", responses = {
//        @ApiResponse(responseCode = "200", description = "Successful operation"),
//        @ApiResponse(responseCode = "400", description = "Invalid data"),
//        @ApiResponse(responseCode = "409", description = "Already exists") })
    @PostMapping(value = "")
    @Operation(summary = "Crea una nueva prenda")
    public ResponseEntity<?> add(@RequestBody Prendas prenda) {
        logger.info("CREANDO UNA PRENDA");
        //S,M,L        
        Pattern pat = Pattern.compile("^[S,M,L]{1}[a-zA-Z0-9]{9}");
        Matcher mat = pat.matcher(prenda.getReferencia());
        if (mat.matches()) {
            logger.info("REF OK");
            prenda.setPrecio(formato(prenda.getPrecio()));
            prenda.setPrecio_promocionado(prenda.getPrecio());
            Prendas newPrenda = prendasService.add(prenda);
            if (newPrenda != null) {
                logger.info((newPrenda.toString()));
                logger.info("PRENDA CREADA " + HttpStatus.CREATED.value());
                return ResponseEntity.status(HttpStatus.CREATED).body(newPrenda);
            } else {
                logger.info("PRENDA NO CREADA " + HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PrendaResponse("PRENDA NO CREADA"));
            }
        } else {
            logger.info("PRENDA NO CREADA ERROR REFERENCIA " + HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new PrendaResponse("LA REF DE LA PRENDA DEBE COMENZAR POR S,M,L Y TENER UNA LONGITUD MÁXIMA DE 10 CARACTERES "));
        }
    }

    @ExceptionHandler({InvalidFormatException.class})
    public ResponseEntity<?> capturExceptionCategoria(InvalidFormatException ex) {
        logger.info("ERROR EN LA RECEPCION DE LA CATEGORIA " + HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PrendaResponse("EL VALOR DE CATEGORIA NO COINCIDE CON NINGUNO DE LOS ACEPTADOS"));
    }

    @DeleteMapping(value = "/{referencia}")
    @Operation(summary = "Elimina una prenda")
    public ResponseEntity<?> delete(@PathVariable String referencia) {
        logger.info("BORRANDO UNA PRENDA");
        Prendas prenda = prendasService.findByReference(referencia);
        if (prenda != null) {
            prendasService.delete(referencia);
            logger.info("PRENDA BORRADA " + HttpStatus.NO_CONTENT.value());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new PrendaResponse("PRENDA ELIMINADA CORRECTAMENTE"));
        } else {
            logger.info("ERROR PRENDA NO ENCONTRADA");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PrendaResponse("PRENDA NO ENCONTRADA " + HttpStatus.NOT_FOUND.value()));
        }
    }

    @GetMapping(value = "")
    @Operation(summary = "Encuentra todas las prendas")
    public ResponseEntity<List<Prendas>> findAll() {
        List<Prendas> prendas = prendasService.findAll();
        logger.info(("PRENDAS ENCONTRADAS " + HttpStatus.OK.value()));
        return ResponseEntity.status(HttpStatus.OK).body(prendas);
    }

    @GetMapping(value = "/{ref}")
    @Operation(summary = "Encuentra una prenda por la referencia")
    public ResponseEntity<?> findByReference(@PathVariable String ref) {
        Prendas prenda = prendasService.findByReference(ref);
        if (prenda != null) {
            logger.info("PRENDA ENCONTRADA " + HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(prenda);
        } else {
            logger.info("PRENDA NO ENCONTRADA " + HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PrendaResponse("PRENDA NO ENCONTRADA"));
        }
    }

    private double formato(double precio) {
        BigDecimal bd = new BigDecimal(precio);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();        
    }

}

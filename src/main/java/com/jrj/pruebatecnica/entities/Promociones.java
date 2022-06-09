package com.jrj.pruebatecnica.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Promociones implements Serializable{

    @Id
    @ApiModelProperty(notes = "Nombre de la promocion", example = "BLACK FRIDAY", required = true)
    private String nombre;
    @ApiModelProperty(notes = "Descuento de la promocion", example = "10.00", required = true)
    private BigDecimal descuento;

    @JsonIgnore
    @ManyToMany(mappedBy = "promocionesDePrendas")     
    private Set<Prendas> prendasPromocion = new HashSet<>();

    public Promociones() {
    }

    public Promociones(String nombre, BigDecimal descuento) {
        this.nombre = nombre;
        this.descuento = descuento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public Set<Prendas> getPrendasPromocion() {
        return prendasPromocion;
    }

    public void setPrendasPromocion(Set<Prendas> prendasPromocion) {
        this.prendasPromocion = prendasPromocion;
    }

    @Override
    public String toString() {
        return "Promociones{" + "nombre=" + nombre + ", descuento=" + descuento + ", prendasPromocion=" + prendasPromocion + '}';
    }

    
    
    
}

package com.jrj.pruebatecnica.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Promociones implements Serializable{

    @Id
    private String nombre;
    private double descuento;

    @JsonIgnore
    @ManyToMany(mappedBy = "promocionesDePrendas")     
    private Set<Prendas> prendasPromocion = new HashSet<>();

    public Promociones() {
    }

    public Promociones(String nombre, double descuento) {
        this.nombre = nombre;
        this.descuento = descuento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
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

package com.jrj.pruebatecnica.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class Prendas implements Serializable {

    @Id    
    @ApiModelProperty(notes = "Referencia de la prenda", example = "S123456789", required = true)     
    private String referencia;
    @ApiModelProperty(notes = "Precio de la prenda", example = "10.15", required = true)
    private BigDecimal precio;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal precio_promocionado;

    @Column(name = "Categorias")
    @ElementCollection(targetClass = Categoria.class)
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(notes = "Categoria de la prenda", required = false)    
    private Set<Categoria> categorias = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "Prendas_promociones",
            joinColumns = {
                @JoinColumn(name = "referencia_prenda")},
            inverseJoinColumns = {
                @JoinColumn(name = "nombre_promo")}
    )
    private Set<Promociones> promocionesDePrendas = new HashSet<>();

    public Prendas() {
    }

    public Prendas(String referencia, BigDecimal precio, BigDecimal precio_promocionado, Set<Categoria> categorias) {
        this.referencia = referencia;
        this.precio = precio;
        this.precio_promocionado = precio_promocionado;
        this.categorias = categorias;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getPrecio() {

        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getPrecio_promocionado() {

        return precio_promocionado;
    }
    
    
    public void setPrecio_promocionado(BigDecimal precio_promocionado) {
        this.precio_promocionado = precio_promocionado;
    }

    public Set<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(Set<Categoria> categorias) {
        this.categorias = categorias;
    }

    public Set<Promociones> getPromocionesDePrendas() {
        return promocionesDePrendas;
    }

    public void setPromocionesDePrendas(Set<Promociones> promocionesDePrendas) {
        this.promocionesDePrendas = promocionesDePrendas;
    }

    @Override
    public String toString() {
        return "Prendas{" + "referencia=" + referencia + ", precio=" + precio + ", precio_promocionado=" + precio_promocionado + ", categorias=" + categorias + ", promocionesDePrendas=" + promocionesDePrendas + '}';
    }

}

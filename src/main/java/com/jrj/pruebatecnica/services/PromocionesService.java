
package com.jrj.pruebatecnica.services;

import com.jrj.pruebatecnica.entities.Promociones;


public interface PromocionesService {
    public Promociones add(Promociones promo);
    public void delete(String nombre);
    public Promociones findByName(String nombre);
}

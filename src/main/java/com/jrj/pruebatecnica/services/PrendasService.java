
package com.jrj.pruebatecnica.services;

import com.jrj.pruebatecnica.entities.Prendas;
import com.jrj.pruebatecnica.entities.Promociones;
import java.util.List;

public interface PrendasService {
    public Prendas add(Prendas prenda);
    public void delete(String ref);
    public List<Prendas> findAll();
    public Prendas findByReference(String ref);
    public Prendas applyPromo(Prendas prenda,Promociones promo);
    public Prendas unapplyPromo(Prendas prenda,Promociones promo);
    
}


package com.jrj.pruebatecnica.services;

import com.jrj.pruebatecnica.entities.Promociones;
import java.util.HashMap;


public interface PromocionesService {
    public Promociones add(Promociones promo);
    public void delete(String nombre);
    public Promociones findByName(String nombre);
    public HashMap<String,Object> apply(String nombre,String referencia);
    public HashMap<String,Object> unapply(String nombre,String referencia);
}

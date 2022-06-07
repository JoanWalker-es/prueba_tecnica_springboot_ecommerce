
package com.jrj.pruebatecnica.services;

import com.jrj.pruebatecnica.entities.Prendas;
import java.util.List;

public interface PrendasService {
    public Prendas add(Prendas prenda);
    public void delete(String ref);
    public List<Prendas> findAll();
    public Prendas findByReference(String ref);
    
    
}

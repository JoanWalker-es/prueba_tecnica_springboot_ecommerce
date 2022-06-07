
package com.jrj.pruebatecnica.services.jpa;

import com.jrj.pruebatecnica.entities.Prendas;
import com.jrj.pruebatecnica.entities.Promociones;
import com.jrj.pruebatecnica.repositories.PrendasRepository;
import com.jrj.pruebatecnica.services.PrendasService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class PrendasServiceJpaImpl implements PrendasService{
    
    private static Logger logger=LoggerFactory.getLogger(PrendasServiceJpaImpl.class);
    
    @Autowired
    private PrendasRepository pr;

    @Override
    public Prendas add(Prendas prenda) {
        logger.info("Creating Prenda");
        return pr.save(prenda);
    }

    @Override
    public void delete(String ref) {
        logger.info("Deleting Prenda");
        pr.deleteById(ref);
    }

    @Override
    public List<Prendas> findAll() {
        logger.info("All Prendas");
        return pr.findAll();
    }

    @Override
    public Prendas findByReference(String ref) {
        logger.info("Find by ref");
        Optional<Prendas> prenda=pr.findById(ref);
        if(prenda.isPresent()){
            logger.info("Found prenda");
            return prenda.get();
        }else{
            logger.info("NOT Found prenda");
            return null;
        }
    }

    @Override
    public Prendas applyPromo(Prendas prenda, Promociones promo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Prendas unapplyPromo(Prendas prenda, Promociones promo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    
    
    
}

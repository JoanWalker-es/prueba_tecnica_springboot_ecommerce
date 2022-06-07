package com.jrj.pruebatecnica.services.jpa;

import com.jrj.pruebatecnica.entities.Promociones;
import com.jrj.pruebatecnica.repositories.PrendasRepository;
import com.jrj.pruebatecnica.repositories.PromocionesRepository;
import com.jrj.pruebatecnica.services.PromocionesService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PromocionesServiceJpaImpl implements PromocionesService {

    private static Logger logger = LoggerFactory.getLogger(PromocionesServiceJpaImpl.class);

    @Autowired
    private PromocionesRepository promoRepo;

    @Autowired
    private PrendasRepository prendasRepo;

    @Override
    public Promociones add(Promociones promo) {
        logger.info(("Creating promo"));
        return promoRepo.save(promo);
    }

    @Override
    public void delete(String nombre) {
        logger.info(("Deleting promo"));
        promoRepo.deleteById(nombre);
    }

    @Override
    public Promociones findByName(String nombre) {
        logger.info("Buscando una promo");
        Optional<Promociones> promo = promoRepo.findById(nombre);
        if (promo.isPresent()) {
            logger.info("Found Promocion");
            return promo.get();
        } else {
            logger.info("NOT Found promocion");
            return null;
        }
    }

}
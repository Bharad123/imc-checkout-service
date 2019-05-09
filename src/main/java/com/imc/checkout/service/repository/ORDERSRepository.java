package com.imc.checkout.service.repository;

import com.imc.checkout.service.domain.ORDERS;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ORDERS entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ORDERSRepository extends JpaRepository<ORDERS, Long> {

}

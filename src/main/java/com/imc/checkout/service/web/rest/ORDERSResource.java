package com.imc.checkout.service.web.rest;

import com.imc.checkout.service.domain.ORDERS;
import com.imc.checkout.service.repository.ORDERSRepository;
import com.imc.checkout.service.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.imc.checkout.service.domain.ORDERS}.
 */
@RestController
@RequestMapping("/api")
public class ORDERSResource {

    private final Logger log = LoggerFactory.getLogger(ORDERSResource.class);

    private static final String ENTITY_NAME = "imcCheckoutServiceOrders";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ORDERSRepository oRDERSRepository;

    public ORDERSResource(ORDERSRepository oRDERSRepository) {
        this.oRDERSRepository = oRDERSRepository;
    }

    /**
     * {@code POST  /orders} : Create a new oRDERS.
     *
     * @param oRDERS the oRDERS to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new oRDERS, or with status {@code 400 (Bad Request)} if the oRDERS has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/orders")
    public ResponseEntity<ORDERS> createORDERS(@RequestBody ORDERS oRDERS) throws URISyntaxException {
        log.debug("REST request to save ORDERS : {}", oRDERS);
        if (oRDERS.getId() != null) {
            throw new BadRequestAlertException("A new oRDERS cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ORDERS result = oRDERSRepository.save(oRDERS);
        return ResponseEntity.created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /orders} : Updates an existing oRDERS.
     *
     * @param oRDERS the oRDERS to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated oRDERS,
     * or with status {@code 400 (Bad Request)} if the oRDERS is not valid,
     * or with status {@code 500 (Internal Server Error)} if the oRDERS couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/orders")
    public ResponseEntity<ORDERS> updateORDERS(@RequestBody ORDERS oRDERS) throws URISyntaxException {
        log.debug("REST request to update ORDERS : {}", oRDERS);
        if (oRDERS.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ORDERS result = oRDERSRepository.save(oRDERS);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, oRDERS.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /orders} : get all the oRDERS.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of oRDERS in body.
     */
    @GetMapping("/orders")
    public List<ORDERS> getAllORDERS() {
        log.debug("REST request to get all ORDERS");
        return oRDERSRepository.findAll();
    }

    /**
     * {@code GET  /orders/:id} : get the "id" oRDERS.
     *
     * @param id the id of the oRDERS to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the oRDERS, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<ORDERS> getORDERS(@PathVariable Long id) {
        log.debug("REST request to get ORDERS : {}", id);
        Optional<ORDERS> oRDERS = oRDERSRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(oRDERS);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" oRDERS.
     *
     * @param id the id of the oRDERS to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteORDERS(@PathVariable Long id) {
        log.debug("REST request to delete ORDERS : {}", id);
        oRDERSRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}

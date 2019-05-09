package com.imc.checkout.service.web.rest;

import com.imc.checkout.service.ImcCheckoutServiceApp;
import com.imc.checkout.service.domain.ORDERS;
import com.imc.checkout.service.repository.ORDERSRepository;
import com.imc.checkout.service.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.imc.checkout.service.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link ORDERSResource} REST controller.
 */
@SpringBootTest(classes = ImcCheckoutServiceApp.class)
public class ORDERSResourceIT {

    private static final Instant DEFAULT_CHECKED_OUT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CHECKED_OUT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CHECKED_OUT_BY = "AAAAAAAAAA";
    private static final String UPDATED_CHECKED_OUT_BY = "BBBBBBBBBB";

    private static final Long DEFAULT_ORDER_NUMBER = 1L;
    private static final Long UPDATED_ORDER_NUMBER = 2L;

    @Autowired
    private ORDERSRepository oRDERSRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restORDERSMockMvc;

    private ORDERS oRDERS;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ORDERSResource oRDERSResource = new ORDERSResource(oRDERSRepository);
        this.restORDERSMockMvc = MockMvcBuilders.standaloneSetup(oRDERSResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ORDERS createEntity(EntityManager em) {
        ORDERS oRDERS = new ORDERS()
            .checkedOutDate(DEFAULT_CHECKED_OUT_DATE)
            .checkedOutBy(DEFAULT_CHECKED_OUT_BY)
            .orderNumber(DEFAULT_ORDER_NUMBER);
        return oRDERS;
    }

    @BeforeEach
    public void initTest() {
        oRDERS = createEntity(em);
    }

    @Test
    @Transactional
    public void createORDERS() throws Exception {
        int databaseSizeBeforeCreate = oRDERSRepository.findAll().size();

        // Create the ORDERS
        restORDERSMockMvc.perform(post("/api/orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(oRDERS)))
            .andExpect(status().isCreated());

        // Validate the ORDERS in the database
        List<ORDERS> oRDERSList = oRDERSRepository.findAll();
        assertThat(oRDERSList).hasSize(databaseSizeBeforeCreate + 1);
        ORDERS testORDERS = oRDERSList.get(oRDERSList.size() - 1);
        assertThat(testORDERS.getCheckedOutDate()).isEqualTo(DEFAULT_CHECKED_OUT_DATE);
        assertThat(testORDERS.getCheckedOutBy()).isEqualTo(DEFAULT_CHECKED_OUT_BY);
        assertThat(testORDERS.getOrderNumber()).isEqualTo(DEFAULT_ORDER_NUMBER);
    }

    @Test
    @Transactional
    public void createORDERSWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = oRDERSRepository.findAll().size();

        // Create the ORDERS with an existing ID
        oRDERS.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restORDERSMockMvc.perform(post("/api/orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(oRDERS)))
            .andExpect(status().isBadRequest());

        // Validate the ORDERS in the database
        List<ORDERS> oRDERSList = oRDERSRepository.findAll();
        assertThat(oRDERSList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllORDERS() throws Exception {
        // Initialize the database
        oRDERSRepository.saveAndFlush(oRDERS);

        // Get all the oRDERSList
        restORDERSMockMvc.perform(get("/api/orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(oRDERS.getId().intValue())))
            .andExpect(jsonPath("$.[*].checkedOutDate").value(hasItem(DEFAULT_CHECKED_OUT_DATE.toString())))
            .andExpect(jsonPath("$.[*].checkedOutBy").value(hasItem(DEFAULT_CHECKED_OUT_BY.toString())))
            .andExpect(jsonPath("$.[*].orderNumber").value(hasItem(DEFAULT_ORDER_NUMBER.intValue())));
    }
    
    @Test
    @Transactional
    public void getORDERS() throws Exception {
        // Initialize the database
        oRDERSRepository.saveAndFlush(oRDERS);

        // Get the oRDERS
        restORDERSMockMvc.perform(get("/api/orders/{id}", oRDERS.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(oRDERS.getId().intValue()))
            .andExpect(jsonPath("$.checkedOutDate").value(DEFAULT_CHECKED_OUT_DATE.toString()))
            .andExpect(jsonPath("$.checkedOutBy").value(DEFAULT_CHECKED_OUT_BY.toString()))
            .andExpect(jsonPath("$.orderNumber").value(DEFAULT_ORDER_NUMBER.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingORDERS() throws Exception {
        // Get the oRDERS
        restORDERSMockMvc.perform(get("/api/orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateORDERS() throws Exception {
        // Initialize the database
        oRDERSRepository.saveAndFlush(oRDERS);

        int databaseSizeBeforeUpdate = oRDERSRepository.findAll().size();

        // Update the oRDERS
        ORDERS updatedORDERS = oRDERSRepository.findById(oRDERS.getId()).get();
        // Disconnect from session so that the updates on updatedORDERS are not directly saved in db
        em.detach(updatedORDERS);
        updatedORDERS
            .checkedOutDate(UPDATED_CHECKED_OUT_DATE)
            .checkedOutBy(UPDATED_CHECKED_OUT_BY)
            .orderNumber(UPDATED_ORDER_NUMBER);

        restORDERSMockMvc.perform(put("/api/orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedORDERS)))
            .andExpect(status().isOk());

        // Validate the ORDERS in the database
        List<ORDERS> oRDERSList = oRDERSRepository.findAll();
        assertThat(oRDERSList).hasSize(databaseSizeBeforeUpdate);
        ORDERS testORDERS = oRDERSList.get(oRDERSList.size() - 1);
        assertThat(testORDERS.getCheckedOutDate()).isEqualTo(UPDATED_CHECKED_OUT_DATE);
        assertThat(testORDERS.getCheckedOutBy()).isEqualTo(UPDATED_CHECKED_OUT_BY);
        assertThat(testORDERS.getOrderNumber()).isEqualTo(UPDATED_ORDER_NUMBER);
    }

    @Test
    @Transactional
    public void updateNonExistingORDERS() throws Exception {
        int databaseSizeBeforeUpdate = oRDERSRepository.findAll().size();

        // Create the ORDERS

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restORDERSMockMvc.perform(put("/api/orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(oRDERS)))
            .andExpect(status().isBadRequest());

        // Validate the ORDERS in the database
        List<ORDERS> oRDERSList = oRDERSRepository.findAll();
        assertThat(oRDERSList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteORDERS() throws Exception {
        // Initialize the database
        oRDERSRepository.saveAndFlush(oRDERS);

        int databaseSizeBeforeDelete = oRDERSRepository.findAll().size();

        // Delete the oRDERS
        restORDERSMockMvc.perform(delete("/api/orders/{id}", oRDERS.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<ORDERS> oRDERSList = oRDERSRepository.findAll();
        assertThat(oRDERSList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ORDERS.class);
        ORDERS oRDERS1 = new ORDERS();
        oRDERS1.setId(1L);
        ORDERS oRDERS2 = new ORDERS();
        oRDERS2.setId(oRDERS1.getId());
        assertThat(oRDERS1).isEqualTo(oRDERS2);
        oRDERS2.setId(2L);
        assertThat(oRDERS1).isNotEqualTo(oRDERS2);
        oRDERS1.setId(null);
        assertThat(oRDERS1).isNotEqualTo(oRDERS2);
    }
}

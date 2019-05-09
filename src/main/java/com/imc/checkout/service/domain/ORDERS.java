package com.imc.checkout.service.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A ORDERS.
 */
@Entity
@Table(name = "orders")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ORDERS implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "checked_out_date")
    private Instant checkedOutDate;

    @Column(name = "checked_out_by")
    private String checkedOutBy;

    @Column(name = "order_number")
    private Long orderNumber;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCheckedOutDate() {
        return checkedOutDate;
    }

    public ORDERS checkedOutDate(Instant checkedOutDate) {
        this.checkedOutDate = checkedOutDate;
        return this;
    }

    public void setCheckedOutDate(Instant checkedOutDate) {
        this.checkedOutDate = checkedOutDate;
    }

    public String getCheckedOutBy() {
        return checkedOutBy;
    }

    public ORDERS checkedOutBy(String checkedOutBy) {
        this.checkedOutBy = checkedOutBy;
        return this;
    }

    public void setCheckedOutBy(String checkedOutBy) {
        this.checkedOutBy = checkedOutBy;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public ORDERS orderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ORDERS)) {
            return false;
        }
        return id != null && id.equals(((ORDERS) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ORDERS{" +
            "id=" + getId() +
            ", checkedOutDate='" + getCheckedOutDate() + "'" +
            ", checkedOutBy='" + getCheckedOutBy() + "'" +
            ", orderNumber=" + getOrderNumber() +
            "}";
    }
}

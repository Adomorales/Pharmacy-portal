package com.pharmacy.dao;

import com.pharmacy.model.Fulfillment;
import java.util.List;

public interface FulfillmentDao {
    Fulfillment getById(long fulfillmentId);
    List<Fulfillment> getByPrescriptionId(long prescriptionId);
    List<Fulfillment> getByProductId(long productId);
    Fulfillment create(Fulfillment fulfillment);
    Fulfillment update(Fulfillment fulfillment);
    boolean delete(long fulfillmentId);
}

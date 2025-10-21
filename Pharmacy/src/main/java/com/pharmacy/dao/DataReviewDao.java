package com.pharmacy.dao;

import com.pharmacy.model.DataReview;

import java.util.List;

public interface DataReviewDao {

    DataReview getById(long dataReviewId);

    List<DataReview> listByDataEntry(long dataEntryId);

    DataReview create(DataReview r);

}

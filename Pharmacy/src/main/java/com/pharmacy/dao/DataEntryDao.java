package com.pharmacy.dao;

import com.pharmacy.model.DataEntry;

import java.util.List;

public interface DataEntryDao {

    DataEntry getById(long entryId);

    List<DataEntry> listQueuePage();

    DataEntry create(DataEntry d);

    DataEntry update(DataEntry d);

}

package edu.scranton.gallaghert8.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.scranton.gallaghert8.objects.LineItem;

@Dao
public interface LineItemDao {
    @Insert
    public void insertLineItem(LineItem lineItem);

    @Insert
    public void insertListLineItems(List<LineItem> lineItems);

    @Query("DELETE FROM line_item")
    public void deleteAllLineItems();

    @Query("SELECT * FROM line_item WHERE orderId = :orderId")
    public LiveData<List<LineItem>> getLineItemsLiveByOrderId(String orderId);

    @Query("SELECT * FROM line_item WHERE orderId = :orderId")
    public List<LineItem> getLineItemsByOrderId(String orderId);
}

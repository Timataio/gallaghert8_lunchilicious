package edu.scranton.gallaghert8.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.scranton.gallaghert8.objects.Order;

@Dao
public interface OrderDao {
    @Insert
    public void insertOrder(Order order);

    @Insert
    public void insertListOrders(List<Order> orders);

    @Query("DELETE FROM lunch_order")
    public void deleteAllOrders();

    @Query("SELECT * FROM lunch_order WHERE orderId = :orderId")
    public LiveData<Order> getOrderLiveById(String orderId);

    @Query("SELECT * FROM lunch_order WHERE orderId = :orderId")
    public Order getOrderById(String orderId);

    @Query("SELECT * FROM lunch_order")
    public LiveData<List<Order>> getAllOrders();

    @Update
    public void updateOrder(Order order);
}

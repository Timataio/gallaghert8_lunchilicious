package edu.scranton.gallaghert8;

import java.util.List;

import edu.scranton.gallaghert8.objects.LineItem;
import edu.scranton.gallaghert8.objects.Order;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OrderClient {
    @GET("orders")
    Call<List<Order>> getAllOrders();

    @GET("order?")
    Call<Order> getOrderById(@Query("orderId") String orderId);

    @GET("lineitems?")
    Call<List<LineItem>> getLineItemsByOrderId(@Query("orderId") String orderId);

    @POST("addorder")
    Call<Order> addOrder(@Body Order order);

    @POST("addlineitems")
    Call<Integer> addLineItems(@Body List<LineItem> lineItems);
}

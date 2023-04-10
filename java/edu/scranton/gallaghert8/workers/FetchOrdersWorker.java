package edu.scranton.gallaghert8.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.util.List;

import edu.scranton.gallaghert8.LunchRoomDatabase;
import edu.scranton.gallaghert8.MenuItemClient;
import edu.scranton.gallaghert8.OrderClient;
import edu.scranton.gallaghert8.dao.LineItemDao;
import edu.scranton.gallaghert8.dao.OrderDao;
import edu.scranton.gallaghert8.objects.LineItem;
import edu.scranton.gallaghert8.objects.MenuItem;
import edu.scranton.gallaghert8.objects.Order;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FetchOrdersWorker extends Worker {
    final static String BASE_URL = "http://aristotle.cs.scranton.edu/lunchilicious/";
    OrderDao orderDao;
    LineItemDao lineItemDao;
    public FetchOrdersWorker(@NonNull Context context,
                                @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
        orderDao = LunchRoomDatabase.getDatabase(context).orderDao();
        lineItemDao = LunchRoomDatabase.getDatabase(context).lineItemDao();
    }
    public Result doWork() {
        Log.d("ORDERS_GET", "Order fetch initiated");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OrderClient client = retrofit.create(OrderClient.class);

        Call<List<Order>> call = client.getAllOrders();
        Response<List<Order>> response = null;
        try {
            response = call.execute();
        }
        catch (IOException e) {
            Log.d("ORDERS_GET", "Call failed, IOException caught");
            return ListenableWorker.Result.retry();
        }
        orderDao.deleteAllOrders();

        List<Order> orders = response.body();
        for(int i=0; i<orders.size(); i++) {
            String orderId = orders.get(i).getOrderId();
            if (orderId.substring(0, 11).equals("gallaghert8")) {
                getLineItems(orderId, client);
            }
            else {
                orders.remove(i);
                i--;
            }
        }
        orderDao.insertListOrders(orders);
        return ListenableWorker.Result.success();
    }
    private void getLineItems(String orderId, OrderClient orderClient) {
        Call<List<LineItem>> callLineItems = orderClient.getLineItemsByOrderId(orderId);
        callLineItems.enqueue(new retrofit2.Callback<List<LineItem>>() {
            @Override
            public void onResponse(Call<List<LineItem>> call, Response<List<LineItem>> response) {
                List<LineItem> lineItems = response.body();
                lineItemDao.insertListLineItems(lineItems);
            }

            public void onFailure(Call<List<LineItem>> call, Throwable t) {
                Log.d("ORDERS_GET", "LineItem get failed");
            }
        });
    }
}

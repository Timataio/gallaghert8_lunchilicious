package edu.scranton.gallaghert8.workers;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.scranton.gallaghert8.LunchRoomDatabase;
import edu.scranton.gallaghert8.MainActivity;
import edu.scranton.gallaghert8.MenuViewModel;
import edu.scranton.gallaghert8.OrderClient;
import edu.scranton.gallaghert8.dao.OrderDao;
import edu.scranton.gallaghert8.dao.LineItemDao;
import edu.scranton.gallaghert8.objects.LineItem;
import edu.scranton.gallaghert8.objects.Order;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaceOrderWorker extends Worker {
    private final static String BASE_URL = "http://aristotle.cs.scranton.edu/lunchilicious/";
    private OrderDao orderDao;
    private LineItemDao lineItemDao;
    private String orderId;

    public PlaceOrderWorker(@NonNull Context context,
                                        @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
        LunchRoomDatabase db = LunchRoomDatabase.getDatabase(context);
        orderDao = db.orderDao();
        lineItemDao = db.lineItemDao();

        orderId = workerParameters.getInputData().getString("ORDER_ID");
    }

    public Result doWork() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OrderClient client = retrofit.create(OrderClient.class);
        Log.d("PLACE_ORDER", "Beginning work");

        Order order = orderDao.getOrderById(orderId);
        List<LineItem> lineItems = lineItemDao.getLineItemsByOrderId(orderId);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String timestamp = simpleDateFormat.format(new Date());
        order.setOrderDate(timestamp);

        orderDao.updateOrder(order);

        Call<Order> callOrder = client.addOrder(order);
        try {
            callOrder.execute();
        }
        catch (IOException e) {
            Log.d("PLACE_ORDER", "Adding Order failed, IOException caught");
            return Result.failure();
        }

        Call<Integer> callLineItems = client.addLineItems(lineItems);
        try {
            callLineItems.execute();
        }
        catch (IOException e) {
            Log.d("PLACE_ORDER", "Adding Line Items failed, IOException caught");
            return Result.failure();
        }

        Log.d("PLACE_ORDER", "Line items were successfully added");
        return Result.success();
    }
}

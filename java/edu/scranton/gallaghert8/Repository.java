package edu.scranton.gallaghert8;

import android.app.Application;
import android.util.Log;
import android.view.Menu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.scranton.gallaghert8.dao.LineItemDao;
import edu.scranton.gallaghert8.dao.MenuItemDao;
import edu.scranton.gallaghert8.dao.OrderDao;
import edu.scranton.gallaghert8.dao.ReviewDao;
import edu.scranton.gallaghert8.objects.LineItem;
import edu.scranton.gallaghert8.objects.MenuItem;
import edu.scranton.gallaghert8.objects.Order;
import edu.scranton.gallaghert8.objects.Review;
import edu.scranton.gallaghert8.workers.FetchMenuItemsWorker;
import edu.scranton.gallaghert8.workers.FetchOrdersWorker;
import edu.scranton.gallaghert8.workers.InsertMenuItemWorker;
import edu.scranton.gallaghert8.workers.PlaceOrderWorker;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {

    LunchRoomDatabase db;

    private final String BASE_URL = "http://aristotle.cs.scranton.edu/lunchilicious/";

    private WorkManager workManager;
    private MenuItemDao menuItemDao;
    private ReviewDao reviewDao;
    private OrderDao orderDao;
    private LineItemDao lineItemDao;
    private MenuItemClient menuClient;


    public Repository(Application application) {
        workManager = WorkManager.getInstance(application);

        db = LunchRoomDatabase.getDatabase(application);
        menuItemDao = db.menuItemDao();
        reviewDao = db.reviewDao();
        orderDao = db.orderDao();
        lineItemDao = db.lineItemDao();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        menuClient = retrofit.create(MenuItemClient.class);


        PeriodicWorkRequest dailyItemRefreshRequest = new
                PeriodicWorkRequest.Builder(FetchMenuItemsWorker.class, 15, TimeUnit.MINUTES)
                .build();
        workManager.enqueue(dailyItemRefreshRequest);

        PeriodicWorkRequest dailyOrderRefreshRequest = new
                PeriodicWorkRequest.Builder(FetchOrdersWorker.class, 1, TimeUnit.DAYS)
                .build();
        workManager.enqueue(dailyItemRefreshRequest);

    }


    public LiveData<List<MenuItem>> getMenuItemsLive() {
        return menuItemDao.getAllMenuItemsLive();
    }

    public MenuItem getMenuItemById(int id) { return menuItemDao.getMenuItem(id); }

    public LiveData<MenuItem> getMenuItemLiveById(int id) {
        return menuItemDao.getMenuItemLive(id);
    }

    public List<Review> getReviews(int menuItemId) {
        return reviewDao.getReviewsById(menuItemId);
    }
    public LiveData<List<Review>> getReviewsLive(int menuItemId) {
        return reviewDao.getReviewsLiveById(menuItemId);
    }

    public LiveData<List<Order>> getOrdersLive() {
        return orderDao.getAllOrders();
    }

    public LiveData<Order> getOrderLiveById(String orderId) {
        return orderDao.getOrderLiveById(orderId);
    }

    public LiveData<List<LineItem>> getLineItemsLive(String orderId) {
        return lineItemDao.getLineItemsLiveByOrderId(orderId);
    }

    public void insertMenuItem(MenuItem menuItem) {

        new Thread(() -> {
            Call<MenuItem> call = menuClient.addMenuItem(menuItem);
            try {
                call.execute();
            }
            catch (IOException e) {
                Log.d("ADD_ITEM_REMOTE", "Adding Item failed, IOException caught");
            }
            OneTimeWorkRequest itemRefreshRequest = new
                    OneTimeWorkRequest.Builder(FetchMenuItemsWorker.class)
                    .build();
            workManager.enqueue(itemRefreshRequest);
        }).start();

    }

    public void insertReview(Review review) {
        reviewDao.insertReview(review); }

    public void insertOrder(Order order) {
        new Thread(() -> {
            orderDao.insertOrder(order);
        }).start();
    }

    public void insertLineItem(LineItem lineItem) {
        new Thread(() -> {
            lineItemDao.insertLineItem(lineItem);
        }).start();
    }

    public void placeOrder(Order order) {
        Data data = new Data.Builder()
                .putString("ORDER_ID", order.getOrderId())
                .build();

        OneTimeWorkRequest placeOrderRequest = new
                OneTimeWorkRequest.Builder(PlaceOrderWorker.class)
                .setInputData(data)
                .build();

        workManager.enqueue(placeOrderRequest);
    }

    private void updateMenuItems() {
        OneTimeWorkRequest fetchMenuItemsRequest = new
                OneTimeWorkRequest.Builder(FetchMenuItemsWorker.class)
                .build();

        workManager.enqueue(fetchMenuItemsRequest);
    }

    public void updateOrder(Order order) {
        orderDao.updateOrder(order);
    }
}


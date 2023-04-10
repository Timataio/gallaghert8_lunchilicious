package edu.scranton.gallaghert8;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.scranton.gallaghert8.dao.LineItemDao;
import edu.scranton.gallaghert8.dao.MenuItemDao;
import edu.scranton.gallaghert8.dao.OrderDao;
import edu.scranton.gallaghert8.dao.ReviewDao;
import edu.scranton.gallaghert8.objects.LineItem;
import edu.scranton.gallaghert8.objects.MenuItem;
import edu.scranton.gallaghert8.objects.Order;
import edu.scranton.gallaghert8.objects.Review;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@Database(entities = {MenuItem.class, Review.class, Order.class, LineItem.class},
    version = 4, exportSchema = false)
public abstract class LunchRoomDatabase extends RoomDatabase {
    public abstract MenuItemDao menuItemDao();
    public abstract ReviewDao reviewDao();
    public abstract OrderDao orderDao();
    public abstract LineItemDao lineItemDao();

    private static volatile LunchRoomDatabase INSTANCE;
    private static final int NUM_THREADS = 4;
    final static String BASE_URL = "http://aristotle.cs.scranton.edu/lunchilicious/";
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static MenuItemClient itemClient;
    private static OrderClient orderClient;

    public static ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUM_THREADS);

    public static LunchRoomDatabase getDatabase(final Context context) {
        synchronized (LunchRoomDatabase.class) {
            if (INSTANCE == null) {
                itemClient = retrofit.create(MenuItemClient.class);
                orderClient = retrofit.create(OrderClient.class);
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        LunchRoomDatabase.class, "my_menu")
                        .fallbackToDestructiveMigration()
                        .addCallback(lunchRoomDBCallback)
                        .build();
            }
        }
        return INSTANCE;
    }


    private static RoomDatabase.Callback lunchRoomDBCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    getMenuItems();
                    getOrders();
                    createReviews();
                }
            }).start();
        }

        /*
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            getOrders();
        }

         */
    };




    private static void getMenuItems() {
        MenuItemDao menuItemDao = INSTANCE.menuItemDao();

        Call<List<MenuItem>> callMenuItems = itemClient.getAllMenuItems();
        callMenuItems.enqueue(new retrofit2.Callback<List<MenuItem>>() {
            @Override
            public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
                List<MenuItem> menuItems = response.body();
                databaseWriteExecutor.execute(() -> {
                    menuItemDao.insertListMenuItems(menuItems);
                    Log.d("ITEMS_GET", "NUM_ITEMS: " + menuItems.size());
                });

            }

            @Override
            public void onFailure(Call<List<MenuItem>> call, Throwable t) {
                Log.d("ITEMS_GET", "NUM_ITEMS: " + 0);
            }
        });
    }

    private static void getOrders() {
        OrderDao orderDao = INSTANCE.orderDao();
        databaseWriteExecutor.execute(() -> {
            orderDao.deleteAllOrders();
            INSTANCE.lineItemDao().deleteAllLineItems();
        });

        Call<List<Order>> callOrder = orderClient.getAllOrders();
        callOrder.enqueue(new retrofit2.Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                List<Order> orders = response.body();
                for (int i=0; i<orders.size(); i++) {
                    String orderId = orders.get(i).getOrderId();
                    if (orderId.substring(0, 11).equals("gallaghert8")) {
                        getLineItems(orderId);
                    }
                    else {
                        orders.remove(i);
                        i--;
                    }
                }
                databaseWriteExecutor.execute(() -> {
                    orderDao.insertListOrders(orders);
                });
                Log.d("ORDERS_GET", "NUM_ORDERS: " + orders.size());
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.d("ORDERS_GET", "FAILED");
            }
        });
    }

    public static void getLineItems(String orderId) {
        LineItemDao lineItemDao = INSTANCE.lineItemDao();
        Call<List<LineItem>> callLineItems = orderClient.getLineItemsByOrderId(orderId);
        callLineItems.enqueue(new retrofit2.Callback<List<LineItem>>() {
           @Override
           public void onResponse(Call<List<LineItem>> call, Response<List<LineItem>> response) {
               List<LineItem> lineItems = response.body();
               databaseWriteExecutor.execute(() -> {
                   lineItemDao.insertListLineItems(lineItems);
               });
           }

           public void onFailure(Call<List<LineItem>> call, Throwable t) {
               Log.d("ORDERS_GET", "LineItem get failed");
           }
        });
    }

    private static void createReviews() {
        ArrayList<Review> result = new ArrayList<>();
        result.add(new Review(1, 1,4, "fresh veg and tasty meat, but, the bread too soft"));
        result.add(new Review(1, 2,5, "perfectly delicious"));
        result.add(new Review(1, 3,2, "it was freezing cold"));
        result.add(new Review(2, 1,5, "so delicious"));
        result.add(new Review(2, 2,4, "almost perfect"));
        result.add(new Review(4, 1,4, "too spicy"));
        result.add(new Review(4, 2,5, "so tasty and crispy"));

        ReviewDao reviewDao = INSTANCE.reviewDao();
        databaseWriteExecutor.execute(() -> {
            reviewDao.deleteAllReviews();
            reviewDao.insertListReviews(result);
        });
    }
}

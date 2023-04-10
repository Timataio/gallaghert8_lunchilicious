package edu.scranton.gallaghert8.workers;

import android.content.Context;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.util.List;

import edu.scranton.gallaghert8.LunchRoomDatabase;
import edu.scranton.gallaghert8.MainActivity;
import edu.scranton.gallaghert8.MenuItemClient;
import edu.scranton.gallaghert8.MenuViewModel;
import edu.scranton.gallaghert8.OrderClient;
import edu.scranton.gallaghert8.dao.MenuItemDao;
import edu.scranton.gallaghert8.objects.LineItem;
import edu.scranton.gallaghert8.objects.MenuItem;
import edu.scranton.gallaghert8.objects.Order;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InsertMenuItemWorker extends Worker {
    private final static String BASE_URL = "http://aristotle.cs.scranton.edu/lunchilicious/";
    private MenuItemDao menuItemDao;
    private int itemId;

    public InsertMenuItemWorker(@NonNull Context context,
                            @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
        menuItemDao = LunchRoomDatabase.getDatabase(context).menuItemDao();
        itemId = workerParameters.getInputData().getInt("ITEM_ID", -1);
    }

    public Result doWork() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MenuItemClient client = retrofit.create(MenuItemClient.class);
        Log.d("ADD_ITEM_REMOTE", "Beginning work");

        MenuItem menuItem = menuItemDao.getMenuItem(itemId);

        Call<MenuItem> call = client.addMenuItem(menuItem);
        Response<MenuItem> response = null;
        try {
            response = call.execute();
            menuItemDao.insertMenuItem(response.body());
        } catch (IOException e) {
            Log.d("ADD_ITEM_REMOTE", "Adding Item failed, IOException caught");
            return Result.failure();
        }

        return Result.success();
    }
}

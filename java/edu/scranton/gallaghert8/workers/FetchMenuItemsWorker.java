package edu.scranton.gallaghert8.workers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.util.List;

import edu.scranton.gallaghert8.LunchRoomDatabase;
import edu.scranton.gallaghert8.MenuItemClient;
import edu.scranton.gallaghert8.dao.MenuItemDao;
import edu.scranton.gallaghert8.objects.MenuItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FetchMenuItemsWorker extends Worker {
    final static String BASE_URL = "http://aristotle.cs.scranton.edu/lunchilicious/";
    MenuItemDao menuItemDao;
    public FetchMenuItemsWorker(@NonNull Context context,
                                @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
        menuItemDao = LunchRoomDatabase.getDatabase(context).menuItemDao();
    }
    public Result doWork() {
        Log.d("PERIODIC_FETCH", "Menu Item fetch initiated");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MenuItemClient client = retrofit.create(MenuItemClient.class);

        Call<List<MenuItem>> call = client.getAllMenuItems();
        call.enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
                LunchRoomDatabase.databaseWriteExecutor.execute(() -> {
                    menuItemDao.deleteAllMenuItems();
                    menuItemDao.insertListMenuItems(response.body());
                    Log.d("PERIODIC_FETCH", "ITEMS: " + response.body().size());
                });
            }

            @Override
            public void onFailure(Call<List<MenuItem>> call, Throwable t) {
                Log.d("PERIODIC_FETCH", "Call failed, IOException caught");
            }
        });
        return Result.success();
    }
}

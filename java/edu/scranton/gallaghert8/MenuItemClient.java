package edu.scranton.gallaghert8;

import java.util.List;

import edu.scranton.gallaghert8.objects.MenuItem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MenuItemClient {
    @GET("menuitems")
    Call<List<MenuItem>> getAllMenuItems();

    @GET("menuitem?")
    Call<MenuItem> getMenuItemById(@Query("id") int id);

    @POST("addmenuitem")
    Call<MenuItem> addMenuItem(@Body MenuItem menuItem);

    @PUT("updatemenuitem/{id}")
    Call<MenuItem> updateMenuItem(@Path("id") int id, @Body MenuItem menuItem);

    @DELETE("deletemenuitem/{id}/")
    Call<MenuItem> deleteMenuItem(@Path("id") int id);
}

package edu.scranton.gallaghert8.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.scranton.gallaghert8.objects.MenuItem;

@Dao
public interface MenuItemDao {
    @Insert
    public void insertMenuItem(MenuItem menuItem);

    @Insert
    public void insertListMenuItems(List<MenuItem> menuItems);

    @Query("DELETE FROM menu_item")
    public void deleteAllMenuItems();

    @Query("SELECT * FROM menu_item WHERE id = :id")
    public MenuItem getMenuItem(int id);

    @Query("SELECT * FROM menu_item WHERE id = :id")
    public LiveData<MenuItem> getMenuItemLive(int id);

    @Query("SELECT * FROM menu_item")
    public LiveData<List<MenuItem>> getAllMenuItemsLive();
}

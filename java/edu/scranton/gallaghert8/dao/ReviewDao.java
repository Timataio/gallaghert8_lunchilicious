package edu.scranton.gallaghert8.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.scranton.gallaghert8.objects.Review;

@Dao
public interface ReviewDao {

    @Insert
    public void insertReview(Review review);

    @Insert
    public void insertListReviews(List<Review> reviews);

    @Query("DELETE FROM review")
    public void deleteAllReviews();

    @Query("SELECT * FROM review WHERE menuItemId = :menuItemId")
    public List<Review> getReviewsById(int menuItemId);

    @Query("SELECT * FROM review WHERE menuItemId = :menuItemId")
    public LiveData<List<Review>> getReviewsLiveById(int menuItemId);
}

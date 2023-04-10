package edu.scranton.gallaghert8.objects;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "review", primaryKeys = {"menuItemId", "reviewNum"}, indices = {@Index("menuItemId"), @Index("reviewNum")})
public class Review {
    protected int menuItemId;
    protected int reviewNum;
    protected int rating;                // domain: 1 .. 5
    protected String comments;


    public Review(int menuItemId, int reviewNum, int rating, String comments) {
        this.menuItemId = menuItemId;
        this.reviewNum = reviewNum;
        this.rating = rating;
        this.comments = comments;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public int getReviewNum() {
        return reviewNum;
    }

    public int getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }

    public void setReviewNum(int reviewNum) {
        this.reviewNum = reviewNum;
    }

    public String toString() {
        return "[" + menuItemId + " , " + rating + " , " + comments + "]";
    }
}

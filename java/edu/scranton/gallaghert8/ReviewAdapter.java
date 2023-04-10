package edu.scranton.gallaghert8;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.scranton.gallaghert8.objects.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private ReviewFragment reviewFragment;
    private List<Review> reviews;

    public ReviewAdapter(ReviewFragment reviewFragment, List<Review> reviews) {
        this.reviewFragment = reviewFragment;
        this.reviews = reviews;
    }

    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_view, parent, false);
        return new ReviewHolder(view);
    }

    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        holder.bind(reviews.get(holder.getBindingAdapterPosition()));
    }

    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }

    public void setData(List<Review> reviews) {
        this.reviews = reviews;
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {
        private TextView ratingView;
        private TextView commentView;
        ReviewHolder(@NonNull View reviewView) {
            super(reviewView);
            ratingView = reviewView.findViewById(R.id.ratingView);
            commentView = reviewView.findViewById(R.id.commentView);
        }

        public void bind(Review review) {
            ratingView.setText(String.format(Locale.US, "%d/5", review.getRating()));
            commentView.setText(review.getComments());


        }
    }
}

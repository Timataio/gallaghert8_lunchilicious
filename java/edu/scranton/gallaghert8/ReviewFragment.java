package edu.scranton.gallaghert8;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.scranton.gallaghert8.objects.LineItem;
import edu.scranton.gallaghert8.objects.MenuItem;
import edu.scranton.gallaghert8.objects.Review;


public class ReviewFragment extends Fragment {
    MainActivity mainActivity;
    MenuViewModel menuViewModel;
    ReviewAdapter reviewAdapter;
    int selectedItemId;

    public static ReviewFragment newInstance() {
        return new ReviewFragment();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        menuViewModel = new ViewModelProvider(mainActivity).get(MenuViewModel.class);
        mainActivity = (MainActivity)getContext();

        View fragContainer = inflater.inflate(R.layout.fragment_review, container, false);
        RecyclerView rView = fragContainer.findViewById(R.id.review_list_view);
        rView.setLayoutManager(new LinearLayoutManager(fragContainer.getContext()));
        reviewAdapter = new ReviewAdapter(this, menuViewModel.getSelectedReviews());
        rView.setAdapter(reviewAdapter);
        initializeNewReviewButton(fragContainer);
        initalizeAddToOrderButton(fragContainer);

        return fragContainer;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView nameView = view.findViewById(R.id.detailed_name_view);
        TextView typeView = view.findViewById(R.id.detailed_type_view);
        TextView priceView = view.findViewById(R.id.detailed_price_view);
        TextView descView = view.findViewById(R.id.detailed_description_view);

        menuViewModel.getSelectedItemLive().observe(getViewLifecycleOwner(), new Observer<MenuItem>() {
            @Override
            public void onChanged(MenuItem menuItem) {
                selectedItemId = menuItem.getId();
                nameView.setText(menuItem.getName());
                typeView.setText(menuItem.getType());
                priceView.setText(String.format("%.2f", menuItem.getUnitPrice()));
                descView.setText(menuItem.getDescription());
                //reviewAdapter.setData(menuViewModel.getSelectedReviews());
                //reviewAdapter.notifyDataSetChanged();
            }
        });

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view.findViewById(R.id.reviews_text).setVisibility(View.VISIBLE);
            view.findViewById(R.id.review_list_view).setVisibility(View.VISIBLE);
        }
        else {
            view.findViewById(R.id.reviews_text).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.review_list_view).setVisibility(View.INVISIBLE);
        }

    }

    private void initializeNewReviewButton(View view) {
        view.findViewById(R.id.button_add_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText ratingView = mainActivity.findViewById(R.id.new_review_rating_view);
                EditText commentView = mainActivity.findViewById(R.id.new_review_comment_view);
                boolean badInput = false;

                String ratingStr = ratingView.getText().toString();
                String comment = commentView.getText().toString();
                int rating = -1;

                try {
                    rating = Integer.parseInt(ratingStr);
                }
                catch (NumberFormatException e) {
                    badInput = true;
                    ratingView.setHint(R.string.invalid_rating_text);
                }

                if (rating < 0 || rating > 5) {
                    badInput = true;
                    ratingView.setText("");
                    ratingView.setHint(R.string.invalid_rating_text);
                }
                if (comment.length() == 0) {
                    badInput = true;
                    commentView.setHint(R.string.empty_field_text);
                }

                if (badInput) {
                    Toast.makeText(getContext(), R.string.toast_review_fail, Toast.LENGTH_LONG).show();
                }
                else {
                    menuViewModel.addReview(new Review(selectedItemId, 0, rating, comment));
                    Toast.makeText(getContext(), R.string.toast_review_success, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initalizeAddToOrderButton(View view) {
        view.findViewById(R.id.add_order_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText quantityView = mainActivity.findViewById(R.id.input_num_items_view);
                String quantityStr = quantityView.getText().toString();
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityStr);
                }
                catch (NumberFormatException e) {
                    quantityView.setHint(R.string.invalid_rating_text);
                    Toast.makeText(getContext(), "Error: Quantity must be an integer", Toast.LENGTH_LONG).show();
                    return;
                }
                menuViewModel.addLineItem(new LineItem(selectedItemId, quantity));
                Toast.makeText(getContext(), "Item Added to Order", Toast.LENGTH_LONG).show();
            }
        });
    }
}
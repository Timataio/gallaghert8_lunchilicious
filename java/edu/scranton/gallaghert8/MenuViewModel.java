package edu.scranton.gallaghert8;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import edu.scranton.gallaghert8.objects.LineItem;
import edu.scranton.gallaghert8.objects.MenuItem;
import edu.scranton.gallaghert8.objects.Order;
import edu.scranton.gallaghert8.objects.Review;

public class MenuViewModel extends AndroidViewModel {

    private int selectedItemId;
    private List<Review> selectedReviews;
    private Repository repository;

    private Order currentOrder;
    private List<LineItem> currentLineItems;
    private List<MenuItem> orderMenuItems;
    private MenuItem lastItem;

    private MutableLiveData<List<LineItem>> lineItemsLive = new MutableLiveData<>();
    private MutableLiveData<List<MenuItem>> orderMenuItemsLive = new MutableLiveData<>();
    private LiveData<List<Review>> selectedReviewsData;

    public MenuViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        selectedItemId = 0;
    }

    public LiveData<MenuItem> getSelectedItemLive() {
        return repository.getMenuItemLiveById(selectedItemId);
    }

    public LiveData<List<MenuItem>> getMenuItemsLive() {
        return repository.getMenuItemsLive();
    }

    public List<Review> getSelectedReviews() {
        return selectedReviewsData.getValue();
    }

    public LiveData<List<Order>> getOrdersLive() {
        return repository.getOrdersLive();
    }

    public LiveData<List<LineItem>> getLineItemsLive() {
        return lineItemsLive;
    }

    public LiveData<List<MenuItem>> getOrderMenuItemsLive() {
        return orderMenuItemsLive;
    }

    public MenuItem getMenuItemById(int id) {
        return repository.getMenuItemById(id);
    }

    public void changeSelectedItem(int newId) {
        selectedItemId = newId;
        selectedReviewsData = repository.getReviewsLive(newId);
    }

    public void addMenuItem(MenuItem item) {
        lastItem = item;
        repository.insertMenuItem(item);
    }

    public void addReview(Review review) {
        new Thread(() -> {
            int numReviews = repository.getReviews(selectedItemId).size();
            review.setReviewNum(numReviews + 1);
            repository.insertReview(review);
            selectedReviews = repository.getReviewsLive(selectedItemId).getValue();
        }).start();
    }

    public void createOrder() {
        currentOrder = new Order();
        repository.insertOrder(currentOrder);
        currentLineItems = new ArrayList<LineItem>();
        lineItemsLive.postValue(currentLineItems);
        orderMenuItems = new ArrayList<MenuItem>();
        orderMenuItemsLive.postValue(orderMenuItems);
    }

    public void addLineItem(LineItem lineItem) {
        if (currentOrder == null) {
            createOrder();
        }

        lineItem.setOrderId(currentOrder.getOrderId());
        lineItem.setLineNum(currentLineItems.size());
        currentLineItems.add(lineItem);

        new Thread(() -> {
            MenuItem menuItem = repository.getMenuItemById(lineItem.getItemId());
            orderMenuItems.add(menuItem);
            orderMenuItemsLive.postValue(orderMenuItems);
            updateOrderCost();
        }).start();

        lineItemsLive.postValue(currentLineItems);
        repository.insertLineItem(lineItem);
    }

    public void placeOrder() {
        repository.placeOrder(currentOrder);
        createOrder();
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public List<LineItem> getLineItems() {
        return currentLineItems;
    }

    public MenuItem getLastMenuItem() {
        return lastItem;
    }

    private void updateOrderCost() {
        double cost = 0;
        for (int i=0; i<orderMenuItems.size(); i++) {
            cost += orderMenuItems.get(i).getUnitPrice() * currentLineItems.get(i).getQuantity();
        }
        currentOrder.setTotalCost(cost);
        repository.updateOrder(currentOrder);
    }
}

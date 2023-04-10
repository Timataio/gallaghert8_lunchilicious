package edu.scranton.gallaghert8.objects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "lunch_order", indices = @Index("orderId"))
public class Order {
    @PrimaryKey @NonNull
    private String orderId;
    private String orderDate;  // format yyyy-MM-dd. e.g., 2020-05-15
    private double totalCost;

    public Order() {
        orderId = createOrderId();
    }
    public String getOrderId() {
        return orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setOrderId(String orderId) {this.orderId = orderId;}

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    private String createOrderId() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-HH-mm-ss");
        String timestamp = simpleDateFormat.format(new Date());
        return "gallaghert8-" + timestamp;
    }
}
package edu.scranton.gallaghert8.objects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName="line_item", primaryKeys={"orderId", "lineNum"}, indices={@Index("orderId")})
public class LineItem {
    @NonNull
    private String orderId;

    private int lineNum;
    private int itemId;
    private int quantity;

    public LineItem(int itemId, int quantity) {
        this.itemId = itemId;
        this.lineNum = 0;
        this.quantity = quantity;
    }

    @NonNull
    public String getOrderId() {
        return orderId;
    }

    public int getLineNum() {
        return lineNum;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setLineNum(int lineNum) { this.lineNum = lineNum;}
}
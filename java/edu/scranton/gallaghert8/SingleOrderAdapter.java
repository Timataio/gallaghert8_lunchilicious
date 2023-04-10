package edu.scranton.gallaghert8;

import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.scranton.gallaghert8.objects.LineItem;
import edu.scranton.gallaghert8.objects.MenuItem;
import edu.scranton.gallaghert8.objects.Review;

public class SingleOrderAdapter extends RecyclerView.Adapter<SingleOrderAdapter.SingleOrderHolder> {

    private SingleOrderFragment singleOrderFragment;
    public List<LineItem> lineItems;
    public List<MenuItem> menuItems;

    public SingleOrderAdapter(SingleOrderFragment singleOrderFragment, List<LineItem> lineItems, List<MenuItem> menuItems) {
        this.singleOrderFragment = singleOrderFragment;
        this.lineItems = lineItems;
        this.menuItems = menuItems;
    }

    public SingleOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_item_view, parent, false);
        return new SingleOrderHolder(view);
    }

    public void onBindViewHolder(@NonNull SingleOrderHolder holder, int position) {
        /*
        LineItem lineItem = lineItems.get(holder.getBindingAdapterPosition());
        MenuItem menuItem = menuItems.get(holder.getBindingAdapterPosition());

         */

        if (menuItems != null) {
            MenuItem menuItem = menuItems.get(holder.getBindingAdapterPosition());
            if (menuItem != null) {
                holder.bind(lineItems.get(holder.getBindingAdapterPosition()),
                        menuItems.get(holder.getBindingAdapterPosition()));
            }
            else {
                Log.d("SINGLE_ORDER", "item is null");
            }
        }
        else {
            Log.d("SINGLE_ORDER", "menu items is null");
        }
    }

    public int getItemCount() {
        return lineItems == null ? 0 : lineItems.size();
    }

    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }


    public class SingleOrderHolder extends RecyclerView.ViewHolder {
        private TextView nameView;
        private TextView priceView;
        private TextView quantityView;
        private TextView totalView;

        SingleOrderHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.line_item_name);
            priceView = itemView.findViewById(R.id.line_item_unit_price);
            quantityView = itemView.findViewById(R.id.line_item_quantity);
            totalView = itemView.findViewById(R.id.line_item_total);
        }

        public void bind(LineItem lineItem, MenuItem menuItem) {
            nameView.setText(menuItem.getName());
            priceView.setText(String.format("%.2f", menuItem.getUnitPrice()));
            quantityView.setText(String.format("%d", lineItem.getQuantity()));
            totalView.setText(String.format("%.2f", (menuItem.getUnitPrice() * lineItem.getQuantity())));
        }


    }
}

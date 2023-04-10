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

import edu.scranton.gallaghert8.MenuViewModel;
import edu.scranton.gallaghert8.R;
import edu.scranton.gallaghert8.SingleOrderAdapter;
import edu.scranton.gallaghert8.SingleOrderFragment;
import edu.scranton.gallaghert8.objects.LineItem;
import edu.scranton.gallaghert8.objects.MenuItem;
import edu.scranton.gallaghert8.objects.Order;

public class AllOrdersAdapter extends RecyclerView.Adapter<AllOrdersAdapter.AllOrdersHolder> {
    private AllOrdersFragment allOrdersFragment;
    public List<Order> orders;

    public AllOrdersAdapter(AllOrdersFragment allOrdersFragment, List<Order> orders) {
        this.allOrdersFragment = allOrdersFragment;
    }

    public AllOrdersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_view, parent, false);
        return new AllOrdersHolder(view);
    }

    public void onBindViewHolder(@NonNull AllOrdersHolder holder, int position) {
        holder.bind(orders.get(holder.getBindingAdapterPosition()));
    }

    public int getItemCount() {
        return orders == null ? 0 : orders.size();
    }

    public void setData(List<Order> orders) {
        this.orders = orders;
    }


    public class AllOrdersHolder extends RecyclerView.ViewHolder {
        private TextView idView;
        private TextView dateView;
        private TextView priceView;

        AllOrdersHolder(@NonNull View itemView) {
            super(itemView);
            idView = itemView.findViewById(R.id.order_id_view);
            dateView = itemView.findViewById(R.id.order_date_view);
            priceView = itemView.findViewById(R.id.order_cost_view);
        }

        public void bind(Order order) {
            idView.setText(order.getOrderId());
            dateView.setText(order.getOrderDate());
            priceView.setText(String.format("%.2f", order.getTotalCost()));
        }

    }
}
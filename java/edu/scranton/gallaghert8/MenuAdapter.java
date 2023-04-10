package edu.scranton.gallaghert8;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.scranton.gallaghert8.objects.MenuItem;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder>{
    public MenuFragment menuFragment;
    public List<MenuItem> items;

    public MenuAdapter(MenuFragment menuFragment, List<MenuItem> items) {
        this.menuFragment = menuFragment;
        this.items = items;
    }

    public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_view, parent, false);
        return new MenuHolder(view);
    }

    public void onBindViewHolder(@NonNull MenuHolder holder, int position) {
        holder.bind(items.get(holder.getBindingAdapterPosition()));
    }

    public void setData(List<MenuItem> items) {
        this.items = items;
    }

    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public class MenuHolder extends RecyclerView.ViewHolder {
        private TextView idView;
        private TextView typeView;
        private TextView nameView;
        private TextView priceView;
        MenuHolder(@NonNull View itemView) {
            super(itemView);
            idView = itemView.findViewById(R.id.idView);
            typeView = itemView.findViewById(R.id.typeView);
            nameView = itemView.findViewById(R.id.nameView);
            priceView = itemView.findViewById(R.id.priceView);
        }

        public void bind(MenuItem item) {
            idView.setText(Integer.toString(item.getId()));
            typeView.setText(item.getType());
            nameView.setText(item.getName());
            priceView.setText(String.format("$%.2f", item.getUnitPrice()));

            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    menuFragment.menuViewModel.changeSelectedItem(item.getId());
                    menuFragment.mainActivity.startReviewFragment();
                }
            });
        }
    }
}

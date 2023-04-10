package edu.scranton.gallaghert8;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.scranton.gallaghert8.objects.Order;

public class AllOrdersFragment extends Fragment {
    MainActivity mainActivity;
    MenuViewModel menuViewModel;
    AllOrdersAdapter allOrdersAdapter;

    public static AllOrdersFragment newInstance() { return new AllOrdersFragment(); }

    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        menuViewModel = new ViewModelProvider(mainActivity).get(MenuViewModel.class);
        View fragContainer = inflater.inflate(R.layout.fragment_all_orders, container, false);

        RecyclerView rView = fragContainer.findViewById(R.id.order_recycler);
        rView.setLayoutManager(new LinearLayoutManager(fragContainer.getContext()));
        allOrdersAdapter = new AllOrdersAdapter(this, menuViewModel.getOrdersLive().getValue());
        rView.setAdapter(allOrdersAdapter);

        return fragContainer;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        menuViewModel.getOrdersLive().observe(getViewLifecycleOwner(), new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                allOrdersAdapter.setData(orders);
                allOrdersAdapter.notifyDataSetChanged();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}

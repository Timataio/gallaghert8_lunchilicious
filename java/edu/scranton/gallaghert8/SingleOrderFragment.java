package edu.scranton.gallaghert8;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.scranton.gallaghert8.objects.LineItem;
import edu.scranton.gallaghert8.objects.MenuItem;

public class SingleOrderFragment extends Fragment {
    MainActivity mainActivity;
    MenuViewModel menuViewModel;
    SingleOrderAdapter singleOrderAdapter;

    public static SingleOrderFragment newInstance() {
        return new SingleOrderFragment();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        menuViewModel = new ViewModelProvider(mainActivity).get(MenuViewModel.class);
        View fragContainer = inflater.inflate(R.layout.fragment_single_order, container, false);

        RecyclerView rView = fragContainer.findViewById(R.id.line_item_recycler);
        rView.setLayoutManager(new LinearLayoutManager(fragContainer.getContext()));
        LiveData<List<LineItem>> lineItemsLive = menuViewModel.getLineItemsLive();
        LiveData<List<MenuItem>> menuItemsLive = menuViewModel.getOrderMenuItemsLive();
        singleOrderAdapter = new SingleOrderAdapter(this,
                lineItemsLive.getValue(), menuItemsLive.getValue());
        rView.setAdapter(singleOrderAdapter);

        lineItemsLive.observe(getViewLifecycleOwner(), new Observer<List<LineItem>>() {
            @Override
            public void onChanged(List<LineItem> lineItems) {
                singleOrderAdapter.setLineItems(lineItems);
                singleOrderAdapter.notifyDataSetChanged();
            }
        });

        menuItemsLive.observe(getViewLifecycleOwner(), new Observer<List<MenuItem>>() {
            @Override
            public void onChanged(List<MenuItem> menuItems) {
                singleOrderAdapter.setMenuItems(menuItems);
                singleOrderAdapter.notifyDataSetChanged();
            }
        });

        initializeAllOrdersButton(fragContainer);
        initializeSubmitOrderButton(fragContainer);

        return fragContainer;
    }

    /*
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        menuViewModel.getLineItemsLive().observe(getViewLifecycleOwner(), new Observer<List<LineItem>>() {
            @Override
            public void onChanged(List<LineItem> lineItems) {
                singleOrderAdapter.setLineItems(lineItems);
                singleOrderAdapter.notifyDataSetChanged();
            }
        });

        menuViewModel.getOrderMenuItemsLive().observe(getViewLifecycleOwner(), new Observer<List<MenuItem>>() {
            @Override
            public void onChanged(List<MenuItem> menuItems) {
                singleOrderAdapter.setMenuItems(menuItems);
                singleOrderAdapter.notifyDataSetChanged();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

     */
    public void initializeAllOrdersButton(View view) {
        view.findViewById(R.id.all_orders_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.startAllOrdersFragment();
            }
        });
    }

    public void initializeSubmitOrderButton(View view) {
        view.findViewById(R.id.submit_order_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuViewModel.placeOrder();
                Toast.makeText(getContext(), "Order Successfully Submitted", Toast.LENGTH_LONG).show();
                singleOrderAdapter.setLineItems(new ArrayList<>());
                singleOrderAdapter.setMenuItems(new ArrayList<>());
                singleOrderAdapter.notifyDataSetChanged();
            }
        });
    }

}

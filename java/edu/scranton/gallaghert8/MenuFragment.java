package edu.scranton.gallaghert8;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import edu.scranton.gallaghert8.objects.MenuItem;


public class MenuFragment extends Fragment {
    MainActivity mainActivity;
    MenuViewModel menuViewModel;
    MenuAdapter menuAdapter;

    public MenuFragment() {}

    @NonNull
    public static MenuFragment newInstance() {
        return new MenuFragment();
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
        View fragContainer = inflater.inflate(R.layout.fragment_menu, container, false);

        RecyclerView rView = fragContainer.findViewById(R.id.menu_item_recycler);
        rView.setLayoutManager(new LinearLayoutManager(fragContainer.getContext()));
        menuAdapter = new MenuAdapter(this, menuViewModel.getMenuItemsLive().getValue());
        rView.setAdapter(menuAdapter);

        initializeNewItemButton(fragContainer);
        initializeViewOrderButton(fragContainer);

        return fragContainer;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        menuViewModel.getMenuItemsLive().observe(getViewLifecycleOwner(), new Observer<List<MenuItem>>() {
            @Override
            public void onChanged(List<MenuItem> menuItems) {
                menuAdapter.setData(menuItems);
                menuAdapter.notifyDataSetChanged();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void initializeNewItemButton(View view) {
        view.findViewById(R.id.button_add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nameView = mainActivity.findViewById(R.id.new_item_name_view);
                EditText typeView = mainActivity.findViewById(R.id.new_item_type_view);
                EditText priceView = mainActivity.findViewById(R.id.new_item_price_view);
                EditText descView = mainActivity.findViewById(R.id.new_item_desc_view);
                boolean badInput = false;

                String name = nameView.getText().toString();
                String type = typeView.getText().toString();
                String priceStr = priceView.getText().toString();
                String desc = descView.getText().toString();
                float price = -1;
                try {
                    price = Float.parseFloat(priceStr);
                }
                catch (NumberFormatException e) {
                    badInput = true;
                    priceView.setText("");
                    priceView.setHint(R.string.invalid_price_text);
                }

                if (name.length() == 0) {
                    badInput = true;
                    nameView.setHint(R.string.empty_field_text);
                }
                if (type.length() == 0) {
                    badInput = true;
                    typeView.setHint("Fill this field");
                }
                if (priceStr.length() == 0) {
                    badInput = true;
                    priceView.setHint(R.string.empty_field_text);
                }
                if (desc.length() == 0) {
                    badInput = true;
                    descView.setHint(R.string.empty_field_text);
                }

                if (badInput) {
                    Toast.makeText(getContext(), R.string.toast_item_fail, Toast.LENGTH_LONG).show();
                }
                else {
                    menuViewModel.addMenuItem(new MenuItem(0, type, name, desc, price));
                    Toast.makeText(getContext(), R.string.toast_item_success, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initializeViewOrderButton(View view) {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view.findViewById(R.id.view_order_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.startSingleOrderFragment();
                }
            });
        }
        else {
            view.findViewById(R.id.view_order_button).setVisibility(View.INVISIBLE);
        }
    }
}
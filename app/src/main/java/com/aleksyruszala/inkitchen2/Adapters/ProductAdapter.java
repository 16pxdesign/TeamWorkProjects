package com.aleksyruszala.inkitchen2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.aleksyruszala.inkitchen2.CustomObjects.Product;
import com.aleksyruszala.inkitchen2.R;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Product> products;
    LayoutInflater inflater;

    public ProductAdapter(Context context, ArrayList<Product> products){
        this.context = context;
        this.products = products;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view==null){
            view = inflater.inflate(R.layout.grid_item_product,null);
            TextView name = (TextView)view.findViewById(R.id.productTextView);
            TextView barcode = (TextView)view.findViewById(R.id.barcodeTtextView);
            TextView price = (TextView)view.findViewById(R.id.priceTextView);
            name.setText(products.get(i).getName());
            barcode.setText(products.get(i).getBarcode());
            price.setText("£ " + products.get(i).getPrice());


        }
        return view;
    }
}

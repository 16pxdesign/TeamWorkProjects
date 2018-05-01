package com.aleksyruszala.inkitchen2;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksyruszala.inkitchen2.Adapters.ProductAdapter;
import com.aleksyruszala.inkitchen2.CustomObjects.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Categories extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listofCourses = new ArrayList<String>();
    private String currentUserID, currentCategoryParent;
    private DatabaseReference mDatabaseCategory, mDatabaseProduct;
    private ProductAdapter gridAdapter;
    private ArrayList<Product> productList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
        if(bundle.getString("parentSelected")!=null){
            currentCategoryParent = bundle.getString("parentSelected");
            setTitle(currentCategoryParent);
        }}else{
            currentCategoryParent = "No parent";
            setTitle("Main Category");
        }



        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseCategory = FirebaseDatabase.getInstance().getReference().child("Users").child("Categories").child(currentUserID);
        mDatabaseProduct = FirebaseDatabase.getInstance().getReference().child("Users").child("Products").child(currentUserID);

        ListView coursesListView = (ListView) findViewById(R.id.catList);
        GridView productGrid = (GridView) findViewById(R.id.productGrid);
        FloatingActionButton newCat = (FloatingActionButton) findViewById(R.id.newCat);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listofCourses);
        coursesListView.setAdapter(adapter);
        loadListOfCategories();

        productList = new ArrayList<Product>();
        gridAdapter = new ProductAdapter(this, productList);
        productGrid.setAdapter(gridAdapter);
        loadListOfProducts();

        newCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, AddCategory.class);
                intent.putExtra("currentParent",currentCategoryParent);
                startActivity(intent);
            }
        });

        coursesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView listValue = (TextView) view;
                String categorySelected = listValue.getText().toString();
                Toast.makeText(getApplicationContext(),categorySelected, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Categories.this, Categories.class);
                intent.putExtra("parentSelected",categorySelected);
                startActivity(intent);

            }
        });
    }

    private void loadListOfProducts() {
        mDatabaseProduct.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    Product product = null;
                    String categoryProduct;
                    if(dataSnapshot.child("category").getValue()==null || dataSnapshot.child("category").getValue().toString().isEmpty()  ){
                        categoryProduct = "No parent";
                    }else{
                        categoryProduct = dataSnapshot.child("category").getValue().toString();
                    }
                    if(dataSnapshot.child("barcode").getValue()!=null && categoryProduct.equalsIgnoreCase(currentCategoryParent)){
                        String name = dataSnapshot.child("name").getValue().toString();
                        String barcode = dataSnapshot.child("barcode").getValue().toString();
                        String price = dataSnapshot.child("price").getValue().toString();
                        productList.add(new Product(barcode, name, price));
                        gridAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void loadListOfCategories() {
        mDatabaseCategory.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    String categoryName = null;
                    if(dataSnapshot.child("name").getValue()!=null && dataSnapshot.child("parent").getValue().toString().equalsIgnoreCase(currentCategoryParent)){
                        categoryName = dataSnapshot.child("name").getValue().toString();
                        listofCourses.add(categoryName);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

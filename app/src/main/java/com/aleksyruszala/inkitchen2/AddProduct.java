package com.aleksyruszala.inkitchen2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddProduct extends AppCompatActivity {

    private EditText barcodeEditText, nameProductEditText, priceEditText, amountEditText;
    private Spinner categorySpinner, unitSpinner;
    private String currentUserID, barcode, productName, units, category;
    private double price, amount;
    private Button okButton,cancelButton;
    private DatabaseReference mDatabaseProduct,mDatabaseCategory;
    private Switch keepOnSwitch;
    private Boolean keepOn = false;
    ArrayAdapter<String> categoryAdapter, unitAdapter;
    ArrayList<String> categoryList = new ArrayList<String>();
    ArrayList<String> unitList = new ArrayList<String>();
    TextView unitEndTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        barcodeEditText =(EditText) findViewById(R.id.barCodeNumber);
        nameProductEditText =(EditText) findViewById(R.id.nameOfProduct);
        priceEditText =(EditText) findViewById(R.id.priceEditText);
        amountEditText =(EditText) findViewById(R.id.amountEditText);
        unitSpinner =(Spinner) findViewById(R.id.unitSpinner);
        categorySpinner =(Spinner) findViewById(R.id.categorySpinner);
        okButton = (Button) findViewById(R.id.okButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        keepOnSwitch = (Switch) findViewById(R.id.keepOn);
        unitEndTextView = (TextView) findViewById(R.id.unitEndTextView);


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseProduct = FirebaseDatabase.getInstance().getReference().child("Users").child("Products").child(currentUserID);
        mDatabaseCategory = FirebaseDatabase.getInstance().getReference().child("Users").child("Categories").child(currentUserID);

        categoryList.add("");
        categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryList);
        categorySpinner.setAdapter(categoryAdapter);
        loadCategories();

        unitList.add("Kilograms");
        unitList.add("Liters");
        unitList.add("Pieces");
        unitList.add("Packs");
        unitAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, unitList);
        unitSpinner.setAdapter(unitAdapter);

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = unitList.get(i);
                unitEndTextView.setText(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        keepOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    keepOn = true;
                }else {
                    keepOn = false;
                }
            }
        });

         okButton.setOnClickListener(new View.OnClickListener() {


             @Override
             public void onClick(View view) {
                 barcode = barcodeEditText.getText().toString();
                 productName = nameProductEditText.getText().toString();
                 String priceString = priceEditText.getText().toString();
                 if(!priceString.isEmpty()) price = Double.parseDouble(priceString);
                 String amountString = amountEditText.getText().toString();
                 if(!amountString.isEmpty()) amount = Double.parseDouble(priceString);
                 String categorySelected = null;
                 if(categorySpinner.getSelectedItem()==null){
                     categorySelected = null;
                 }else {
                     categorySelected = categorySpinner.getSelectedItem().toString();
                 }



                 if(!barcode.isEmpty() && !productName.isEmpty() && !priceString.isEmpty()){


                     DatabaseReference mDatabaseProductName = mDatabaseProduct.push();



                     Map product = new HashMap();
                     product.put("barcode",barcode);
                     product.put("name",productName);
                     product.put("price",price);
                     product.put("category",null);
                     mDatabaseProductName.setValue(product);
                     Toast.makeText(getApplicationContext(),"Product added." , Toast.LENGTH_SHORT).show();

                     if(keepOn == false){
                         finish();
                     }

                     barcodeEditText.setText(null);
                     nameProductEditText.setText(null);
                     priceEditText.setText(null);
                 }else{
                     Toast.makeText(getApplicationContext(),"Need to fill all boxes." , Toast.LENGTH_SHORT).show();
                 }


             }
         });
    }

    private void loadCategories() {

            mDatabaseCategory.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(dataSnapshot.exists()){
                        String categoryName = null;
                        if(dataSnapshot.child("name").getValue()!=null){
                            categoryName = dataSnapshot.child("name").getValue().toString();
                            categoryList.add(categoryName);
                            categoryAdapter.notifyDataSetChanged();
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

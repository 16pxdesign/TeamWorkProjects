package com.aleksyruszala.inkitchen2;

import android.content.Intent;
import android.os.Parcelable;
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

import com.aleksyruszala.inkitchen2.CustomObjects.Category;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
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
    private Button okButton,cancelButton,scanButton;
    private DatabaseReference mDatabaseProduct,mDatabaseCategory;
    private Switch keepOnSwitch;
    private Boolean keepOn = false;
    ArrayAdapter<String> unitAdapter;
    ArrayAdapter<Category> categoryAdapter;
    ArrayList<Category> categoryList = new ArrayList<Category>();
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
        scanButton = (Button) findViewById(R.id.scanButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        keepOnSwitch = (Switch) findViewById(R.id.keepOn);
        unitEndTextView = (TextView) findViewById(R.id.unitEndTextView);


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseProduct = FirebaseDatabase.getInstance().getReference().child("Users").child("Products").child(currentUserID);
        mDatabaseCategory = FirebaseDatabase.getInstance().getReference().child("Users").child("Categories").child(currentUserID);

        categoryList.add(new Category(null, "No category"));
        categoryAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, categoryList);
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

                 long selectedItemId = categorySpinner.getSelectedItemId();
                 Category category = categoryList.get((int) selectedItemId);


                 if(!barcode.isEmpty() && !productName.isEmpty() && !priceString.isEmpty()){


                     DatabaseReference mDatabaseProductName = mDatabaseProduct.push();



                     Map product = new HashMap();
                     product.put("barcode",barcode);
                     product.put("name",productName);
                     product.put("price",price);
                     product.put("category",category.getId());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0 ){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data!=null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    barcodeEditText.setText(barcode.displayValue);
                }else{
                    barcodeEditText.setText("NoNumber");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadCategories() {

            mDatabaseCategory.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(dataSnapshot.exists()){
                        String categoryName = null;
                        if(dataSnapshot.child("name").getValue()!=null){
                            categoryName = dataSnapshot.child("name").getValue().toString();
                            categoryList.add(new Category(dataSnapshot.getKey(),categoryName));
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

            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AddProduct.this, BarcodeScanner.class);
                    startActivityForResult(intent,0);
                }
            });


    }
}

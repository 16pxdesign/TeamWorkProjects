package com.aleksyruszala.inkitchen2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aleksyruszala.inkitchen2.CustomObjects.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddCategory extends AppCompatActivity {
    EditText catNameEditText;
    ArrayAdapter<Category> spinnerAdapter;
    ArrayList<Category> arraySpinner;
    Spinner spinner;
    private String currentUserID, currentIdCategoryParent, currentNameCategoryParent;

    DatabaseReference mDatabaseCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseCategory = FirebaseDatabase.getInstance().getReference().child("Users").child("Categories").child(currentUserID);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            if(bundle.getString("currentParent")!=null){
                currentIdCategoryParent = bundle.getString("currentParent");
            }
        }else{
            currentIdCategoryParent = "No parent";
        }


        Button okButton = (Button) findViewById(R.id.okButton);
        Button cancelButton = (Button) findViewById(R.id.okButton);
        spinner = (Spinner) findViewById(R.id.spinner);
        arraySpinner = new ArrayList<>();
        arraySpinner.add(new Category(null, "No Parent"));
        spinnerAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, arraySpinner);
        spinner.setAdapter(spinnerAdapter);

        catNameEditText = (EditText) findViewById(R.id.catNameEditText);



        loadList();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryName = catNameEditText.getText().toString();
                long selectedItemId = spinner.getSelectedItemId();
                Category categoryItem = arraySpinner.get((int) selectedItemId);
                if(!categoryName.isEmpty()){
                    DatabaseReference mDatabaseCategoryName = mDatabaseCategory.push();
                    Map category = new HashMap();
                    category.put("name",categoryName);
                    category.put("parent",categoryItem.getId());
                    mDatabaseCategoryName.setValue(category);
                    Toast.makeText(getApplicationContext(),"Category added.", Toast.LENGTH_SHORT).show();
                }
                catNameEditText.setText(null);
                spinner.setSelection(0);

            }
        });



    }

    private void loadList() {
        mDatabaseCategory.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    String categoryName = null;
                    if(dataSnapshot.child("name").getValue()!=null){
                        categoryName = dataSnapshot.child("name").getValue().toString();
                        arraySpinner.add(new Category(dataSnapshot.getKey(),categoryName));
                        spinnerAdapter.notifyDataSetChanged();

                        if(categoryName.equalsIgnoreCase(currentIdCategoryParent)){
                            int i = arraySpinner.indexOf(currentIdCategoryParent);
                            spinner.setSelection(i);
                        }
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

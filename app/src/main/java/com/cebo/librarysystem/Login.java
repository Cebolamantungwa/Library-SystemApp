package com.cebo.librarysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cebo.librarysystem.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private int unique_number = 0;
    private int unique_password = 0;
    private TextView alert_login;
    private EditText number, password;
    private TextView register;
    private AppCompatButton login_btn;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        alert_login = findViewById(R.id.alert_login);
        // progressDialog
        progressDialog = new ProgressDialog(this);
        //user variables
        number = findViewById(R.id.number);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        //LOGIN button
        login_btn.setOnClickListener(v->{
            login();

        });
        register = findViewById(R.id.register);
        register.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(),SignUp.class));
            Login.this.finish();
        });

    }
    //user verification
    private void login(){

        //creating string variables
        String Number,userPassword;
        Number = number.getText().toString().trim();
        userPassword = password.getText().toString().trim();



       // progressDialog.show();
       if(Number.equals("1234")&&userPassword.equals("admin")){
           startActivity(new Intent(getApplicationContext(),AdminPage.class));
       }
        else if(Number.isEmpty()||userPassword.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please fill all the fields before clicking the button", Toast.LENGTH_SHORT).show();

        }
        else if(!Number.matches("\\d+"))
        {   number.requestFocus();
            number.setError("PLEASE ENTER THE CORRECT USERNAME");

        }
        else if(userPassword.length()<=7)
        {
            password.requestFocus();
            password.setError("Your password is incorrect");

        }
        //if it is a staff member
        else if(Number.length()==5){
           progressDialog.setMessage("Please wait...");
           progressDialog.setTitle("Login");
           progressDialog.setCanceledOnTouchOutside(false);
           progressDialog.show();
           //verifying student number
           DatabaseReference databaseReferenceStaff = FirebaseDatabase.getInstance().getReference("Staff_members");
           databaseReferenceStaff.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {

                   for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                       UserModel Model = dataSnapshot.getValue(UserModel.class);
                       if(Model.getKey_number().equals(Number)&&userPassword.equals(Model.getPassword())){
                           Query user =databaseReferenceStaff.orderByChild("key_number").equalTo(Number);
                           user.addListenerForSingleValueEvent(new ValueEventListener() {

                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   unique_number = 1;
                                   try {
                                       for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                           UserModel Model=dataSnapshot.getValue(UserModel.class);
                                           if(!Model.getKey_number().equals(null)&&Number.equals(Model.getKey_number())){
                                                progressDialog.dismiss();
                                               //verifying password
                                               Query query =databaseReferenceStaff.orderByChild("password").equalTo(userPassword);
                                               query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                       unique_password = 1;
                                                       alert_login.setVisibility(View.GONE);
                                                       try {
                                                           for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                                               UserModel Model=dataSnapshot.getValue(UserModel.class);
                                                               if (!Model.getPassword().equals(null)&&userPassword.equals(Model.getPassword())){

                                                                   startActivity(new Intent(Login.this,HomePage.class));
                                                                   Login.this.finish();
                                                                   progressDialog.dismiss();
                                                                   break;

                                                               }

                                                           }
                                                       }catch (Exception m){
                                                           progressDialog.dismiss();
                                                           m.printStackTrace();
                                                           Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                       }

                                                   }

                                                   @Override
                                                   public void onCancelled(@NonNull DatabaseError error) {

                                                       progressDialog.dismiss();
                                                   }
                                               });
                                               break;

                                           }
                                       }
                                   }catch (Exception p){
                                       progressDialog.dismiss();
                                       p.printStackTrace();
                                       Toast.makeText(Login.this, "Your username or password is incorrect", Toast.LENGTH_SHORT).show();
                                   }


                               }
                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {
                                   progressDialog.dismiss();
                               }
                           });

                       }

                   }
                   if(unique_number!=1||unique_password!=1){
                       progressDialog.dismiss();
                       alert_login.setVisibility(View.VISIBLE);}
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                   progressDialog.dismiss();
               }
           });


        }

        else {
           progressDialog.setMessage("Please wait...");
           progressDialog.setTitle("Login");
           progressDialog.setCanceledOnTouchOutside(false);
           progressDialog.show();
            //verifying student number
           databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        UserModel Model = dataSnapshot.getValue(UserModel.class);
                        if(Model.getKey_number().equals(Number)&&userPassword.equals(Model.getPassword())){
                            Query user =databaseReference.orderByChild("key_number").equalTo(Number);
                            user.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    unique_number=1;
                                    try {
                                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                            UserModel Model=dataSnapshot.getValue(UserModel.class);
                                            if(!Model.getKey_number().equals(null)&&Number.equals(Model.getKey_number())){
                                                // progressDialog.dismiss();
                                                //verifying password
                                                Query query =databaseReference.orderByChild("password").equalTo(userPassword);
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        unique_password = 1;
                                                        alert_login.setVisibility(View.GONE);
                                                        try {
                                                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                                                UserModel Model=dataSnapshot.getValue(UserModel.class);
                                                                if (!Model.getPassword().equals(null)&&userPassword.equals(Model.getPassword())){

                                                                    startActivity(new Intent(Login.this,HomePage.class));
                                                                    Login.this.finish();
                                                                    progressDialog.dismiss();
                                                                    break;
                                                                }

                                                            }
                                                        }catch (Exception m){
                                                            progressDialog.dismiss();
                                                            m.printStackTrace();
                                                            Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                        progressDialog.dismiss();
                                                    }
                                                });

                                            }
                                        }
                                    }catch (Exception p){
                                        progressDialog.dismiss();
                                        p.printStackTrace();
                                        Toast.makeText(Login.this, "Your username or password is incorrect", Toast.LENGTH_SHORT).show();
                                    }


                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.dismiss();
                                }
                            });
                          break;
                        }

                    }if(unique_number!=1||unique_password!=1){
                        progressDialog.dismiss();
                        alert_login.setVisibility(View.VISIBLE);}


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                }
            });


            }

        }
    }

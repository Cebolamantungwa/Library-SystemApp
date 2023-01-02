package com.cebo.librarysystem;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cebo.librarysystem.model.StudentNumbersModels;
import com.cebo.librarysystem.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {
    //variables use to detect a successful execution of a code
    String student_number_rg = "0";
    String staff_number_rg = "0";

    int  stuff = 0;
    int student = 0;
    private EditText initials,surname,student_number,password,confirm_password;
    private AppCompatButton sign_up_btn,Student_btn,Staff_btn;
    private FirebaseAuth firebaseAuth;
    private ImageView back_to_login;
    private TextView alert;
    private LinearLayoutCompat Staff_linear_form,Student_linear_form;
    //staff
    private EditText initials_staff,surname_staff,staff_number,password_staff,confirm_password_staff;
    private AppCompatButton staff_sign_up_btn;
    private TextView alert_staff;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //Linear views layouts for stu... and staff...
        Student_linear_form = findViewById(R.id.Student_linear_form);
        Staff_linear_form = findViewById(R.id.Staff_linear_form);

        //option buttons
        Student_btn = findViewById(R.id.Student_btn);
        Student_btn.setOnClickListener(v->{
            //set form visibility of a student to visible and of a staff member to Gone
            Student_linear_form.setVisibility(View.VISIBLE);
            Staff_linear_form.setVisibility(View.GONE);
        });
        Staff_btn = findViewById(R.id.Staff_btn);
        Staff_btn.setOnClickListener(v->{
            //set form visibility of a staff member to visible and of a student to Gone
            Staff_linear_form.setVisibility(View.VISIBLE);
            Student_linear_form.setVisibility(View.GONE);
        });

        // progressDialog
        progressDialog = new ProgressDialog(this);
        /**Student variables**/
        initials =findViewById(R.id.initials);
        surname =findViewById(R.id.surname);
        student_number =findViewById(R.id.student_number);

        password =findViewById(R.id.password);
        confirm_password =findViewById(R.id.confirm_password);
        sign_up_btn =findViewById(R.id.sign_up_btn);
        sign_up_btn.setOnClickListener(v->{
            studentVerification();
        });
        alert = findViewById(R.id.alert);

        /**end**/
        /**Staff variables**/
        initials_staff =findViewById(R.id.initials_staff);
        surname_staff =findViewById(R.id.surname_staff);
        staff_number =findViewById(R.id.staff_number);

        password_staff =findViewById(R.id.password_staff);
        confirm_password_staff =findViewById(R.id.confirm_password_staff);
        staff_sign_up_btn =findViewById(R.id.staff_sign_up_btn);
        staff_sign_up_btn.setOnClickListener(v->{
            staffVerification();
        });
        /**end**/
        alert_staff = findViewById(R.id.alert_staff);

        back_to_login = findViewById(R.id.contact_back);
        back_to_login.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), Login.class));
            SignUp.this.finish();
        });
    }
    /**Verification of student input and uploading to  Database**/
    public void studentVerification(){
        //creating string variables
        String initial,last_name,ID,Password,ConfirmPassword;


        initial    =initials.getText().toString().trim();
        last_name = surname.getText().toString().trim();
        ID =student_number.getText().toString().trim();

        Password= password.getText().toString().trim();
        ConfirmPassword = confirm_password.getText().toString().trim();
        if(initial.isEmpty()||last_name.isEmpty()||ID.isEmpty()||Password.isEmpty()||ConfirmPassword.isEmpty()){
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
        else if(!initial.matches("[a-zA-Z ]+")){
            Toast.makeText(this, "ENTER ONLY ALPHABETICAL CHARACTER IN THE NAME FIELD", Toast.LENGTH_SHORT).show();
        }
        else if(!last_name.matches("[a-zA-Z ]+")){
            Toast.makeText(this, "ENTER ONLY ALPHABETICAL CHARACTER IN THE LAST NAME FIELD", Toast.LENGTH_SHORT).show();
        }
        else if(ID.length()<=8){
            Toast.makeText(this, "IT SHOULD CONTAIN 9 DIGITS", Toast.LENGTH_SHORT).show();
        }

        else if(Password.length()<=7)
        {
            password.requestFocus();
            password.setError("IT SHOULD CONTAIN 8 OR MORE CHARACTERS");
        }

        else if(!Password.matches(ConfirmPassword)){

            Toast.makeText(this, "the passwords you entered are not the same", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.setMessage("Please wait...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            try{
                DatabaseReference num =  FirebaseDatabase.getInstance().getReference().child("key");
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

                //checking if the student number is already registered
                Query cstudent_number = num.orderByChild("number").equalTo(ID);
                cstudent_number.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        student_number_rg = "1";
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                            StudentNumbersModels model = dataSnapshot.getValue( StudentNumbersModels.class);
                            if(model.getNumber().equals(ID)){

                                //checking if the student number is already registered
                                Query query_student_number = databaseReference.orderByChild("key_number").equalTo(ID);
                                query_student_number.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                            UserModel model = dataSnapshot.getValue( UserModel.class);
                                            if(model.getKey_number().equals(ID)){
                                                student_number_rg = "1";
                                                student =1;
                                                student_number.findFocus();
                                                alert.setVisibility(View.GONE);
                                                student_number.setError("This student number is already registered");
                                                progressDialog.dismiss();
                                                break;
                                            }

                                        }
                                        if(student != 1){
                                            UserModel applicantModel =new UserModel(initial,last_name,ID,Password);
                                            databaseReference.child(ID).setValue(applicantModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    startActivity(new Intent(getApplicationContext(),Login.class));
                                                    SignUp.this.finish();
                                                    progressDialog.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if(student_number_rg.equals("0")){
                    alert.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
                else{
                    alert.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
                }catch (Exception exception){
                progressDialog.dismiss();
                exception.printStackTrace();
                Toast.makeText(this,"Please check your internet connection",Toast.LENGTH_SHORT).show();

            }
        }
    }
    /**Verification of staff input and uploading to  Database**/
    public void staffVerification(){

        //creating string variables
        String initial,last_name,ID,Password,ConfirmPassword;


        initial    =initials_staff.getText().toString().trim();
        last_name = surname_staff.getText().toString().trim();
        ID =staff_number.getText().toString().trim();
        Password= password_staff.getText().toString().trim();
        ConfirmPassword = confirm_password_staff.getText().toString().trim();
        if(initial.isEmpty()||last_name.isEmpty()||ID.isEmpty()||Password.isEmpty()||ConfirmPassword.isEmpty()){
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
        else if(!initial.matches("[a-zA-Z ]+")){
            Toast.makeText(this, "ENTER ONLY ALPHABETICAL CHARACTER IN THE NAME FIELD", Toast.LENGTH_SHORT).show();
        }
        else if(!last_name.matches("[a-zA-Z ]+")){
            Toast.makeText(this, "ENTER ONLY ALPHABETICAL CHARACTER IN THE LAST NAME FIELD", Toast.LENGTH_SHORT).show();
        }
        else if(ID.length()<=4){
            Toast.makeText(this, "IT SHOULD CONTAIN 5 DIGITS", Toast.LENGTH_SHORT).show();
        }

        else if(Password.length()<=7)
        {
            password_staff.requestFocus();
            password_staff.setError("IT SHOULD CONTAIN 8 OR MORE CHARACTERS");
        }

        else if(!Password.matches(ConfirmPassword)){

            Toast.makeText(this, "the passwords you entered are not the same", Toast.LENGTH_SHORT).show();
        }else {

            progressDialog.setMessage("Please wait...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            try{
                DatabaseReference num =  FirebaseDatabase.getInstance().getReference().child("staff_key");
                DatabaseReference databaseReferenceStaff = FirebaseDatabase.getInstance().getReference().child("Staff_members");

                Query cstudent_number = num.orderByChild("number").equalTo(ID);
                cstudent_number.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        staff_number_rg = "1";
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                            StudentNumbersModels model = dataSnapshot.getValue( StudentNumbersModels.class);
                            if(model.getNumber().equals(ID)){

                                //checking if the staff number is already registered
                                Query query_staff_number = databaseReferenceStaff.orderByChild("key_number").equalTo(ID);
                                query_staff_number.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                            UserModel model = dataSnapshot.getValue( UserModel.class);
                                            if(model.getKey_number().equals(ID)){
                                                staff_number_rg = "1";
                                                alert_staff.setVisibility(View.GONE);
                                                stuff =1;
                                                staff_number.findFocus();
                                                staff_number.setError("This staff number is already registered");
                                            }

                                        }
                                        if(stuff != 1){
                                            UserModel applicantModel =new UserModel(initial,last_name,ID,Password);
                                            databaseReferenceStaff.child(ID).setValue(applicantModel).addOnCompleteListener(new OnCompleteListener<Void>(){
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    try {

                                                        if(task.isSuccessful())
                                                        {
                                                          startActivity(new Intent(getApplicationContext(),Login.class));
                                                          SignUp.this.finish();
                                                          progressDialog.dismiss();

                                                        }

                                                    }catch (Exception exception){
                                                        progressDialog.dismiss();
                                                        exception.printStackTrace();
                                                        Toast.makeText(SignUp.this,"This staff number is registered",Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if(staff_number_rg.equals("0")){
                    alert_staff.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
                else{
                    alert.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }

            }catch (Exception exception){
                progressDialog.dismiss();
                exception.printStackTrace();
                Toast.makeText(this,"Please check your internet connection",Toast.LENGTH_SHORT).show();

            }
        }
    }
    @Override
    public void onBackPressed(){
        progressDialog.dismiss();
        startActivity(new Intent(SignUp.this,Login.class));
        SignUp.this.finish();
    }

}
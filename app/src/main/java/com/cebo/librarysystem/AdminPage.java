package com.cebo.librarysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cebo.librarysystem.model.BookModel;
import com.cebo.librarysystem.model.StaffMemberModel;
import com.cebo.librarysystem.model.StudentNumbersModels;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AdminPage extends AppCompatActivity {
    private EditText add_student_number, add_staff_number, Book_title, Book_isbn,remove_book,Book_author;
    private TextView book_loci;
    private AppCompatButton upload, upload_cancel,upload_staff,upload_staff_cancel,Select_book,upload_book
            ,upload_book_cancel,remove_book_btn,remove_book_cancel;
    private DatabaseReference databaseReference,databaseReferenceStaff, databaseReferenceBook;
    private CardView add_student_number_card,add_staff_card, add_book_card, remove_book_card;
    private LinearLayoutCompat add_student_linear, Add_book_linear,add_staff_linear,remove_book_linear;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private int detectNumber = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        progressDialog = new ProgressDialog(this);
        //variables
        /***add student variables**/
        //linear view
        add_student_linear = findViewById(R.id.add_student_linear);
        //student number variable
        add_student_number = findViewById(R.id.add_student_number);

        //add student number buttons
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(view->{
            uploadStudentNumber();
        });
        //add student number cancel buttons
        upload_cancel = findViewById(R.id.upload_cancel);
        upload_cancel.setOnClickListener(view->{
            add_student_number.getText().clear();
            add_student_linear.setVisibility(View.GONE);
        });

        //student numbers database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("key");
        //linear view
        add_student_linear = findViewById(R.id.add_student_linear);
        //cards
        add_student_number_card = findViewById(R.id.add_student_number_card);
        add_student_number_card.setOnClickListener(v->{
            add_student_linear.setVisibility(View.VISIBLE);
            add_staff_linear.setVisibility(View.GONE);
            Add_book_linear.setVisibility(View.GONE);
        });
        /***end student variables**/

        /***add staff variables**/
        //linear view
        add_staff_linear = findViewById(R.id.add_staff_linear);
        //student number variable
        add_staff_number = findViewById(R.id.add_staff_number);

        //add student number buttons
        upload_staff = findViewById(R.id.upload_staff);
        upload_staff.setOnClickListener(view->{
            uploadStaff();
        });
        //add student number cancel buttons
        upload_staff_cancel = findViewById(R.id.upload_staff_cancel);
        upload_staff_cancel.setOnClickListener(view->{
            add_staff_number.getText().clear();
            add_staff_linear.setVisibility(View.GONE);
        });

        //student numbers database reference
        databaseReferenceStaff = FirebaseDatabase.getInstance().getReference().child("staff_key");

        //cards
        add_staff_card = findViewById(R.id.add_staff_card);
        add_staff_card.setOnClickListener(v->{
            add_staff_linear.setVisibility(View.VISIBLE);
            add_student_linear.setVisibility(View.GONE);
            Add_book_linear.setVisibility(View.GONE);
            remove_book_linear.setVisibility(View.GONE);
        });
        /***end staff variables**/

        /***add book variables**/
        //book text view
        book_loci = findViewById(R.id.book_loci);
        //buttons
        Select_book = findViewById(R.id.Select_book);
        Select_book.setOnClickListener(V->{
            selectBook();
        });
        upload_book = findViewById(R.id.upload_book);
        upload_book_cancel = findViewById(R.id.upload_book_cancel);
        upload_book_cancel.setOnClickListener(view->{
            Add_book_linear.setVisibility(View.GONE);
            Book_isbn.getText().clear();
            Book_title.getText().clear();
            Book_author.getText().clear();
        });
        //add book linear
        Add_book_linear = findViewById(R.id.Add_book_linear);
        //add book card view
        add_book_card = findViewById(R.id.add_book_card);
        add_book_card.setOnClickListener(view->{
            Add_book_linear.setVisibility(View.VISIBLE);
            add_student_linear.setVisibility(View.GONE);
            add_staff_linear.setVisibility(View.GONE);
            remove_book_linear.setVisibility(View.GONE);
        });
        //add book linear
        Add_book_linear = findViewById(R.id.Add_book_linear);
        //add book card view
        remove_book_card = findViewById(R.id.remove_book_card);
        add_book_card.setOnClickListener(view->{
            Add_book_linear.setVisibility(View.VISIBLE);
            add_student_linear.setVisibility(View.GONE);
            add_staff_linear.setVisibility(View.GONE);
            remove_book_linear.setVisibility(View.GONE);
        });
        Book_author = findViewById(R.id.Book_author);
        Book_title = findViewById(R.id.Book_title);
        Book_isbn = findViewById(R.id.Book_isbn);
       /***end book variables**/

        /***Remove book variables**/
        //edit text
        remove_book = findViewById(R.id.remove_book);
        //linear
        remove_book_linear = findViewById(R.id.remove_book_linear);
        //card
        remove_book_card = findViewById(R.id.remove_book_card);
        remove_book_card.setOnClickListener(v->{
            remove_book_linear.setVisibility(View.VISIBLE);
            add_staff_linear.setVisibility(View.GONE);
            add_student_linear.setVisibility(View.GONE);
            Add_book_linear.setVisibility(View.GONE);
        });
        //remove book btn
        remove_book_btn = findViewById(R.id.remove_book_btn);
        remove_book_btn.setOnClickListener(v->{
            removeBook();
        });
        //cancel button
        remove_book_cancel = findViewById(R.id.remove_book_cancel);
        remove_book_cancel.setOnClickListener(v->{
            remove_book_linear.setVisibility(View.GONE);
            remove_book.getText().clear();
        });

        /***end**/
    }
    /***Adding a student number in the database module**/
    public void uploadStudentNumber(){
        String student_number;

        student_number = add_student_number.getText().toString().trim();
        if(student_number.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please fill all the fields before clicking the button", Toast.LENGTH_SHORT).show();
        }

        else if(student_number.length()<=8)
        {
            add_student_number.requestFocus();
            add_student_number.setError("IT SHOULD CONTAIN 9 DIGITS");
            //progressDialog.dismiss();
        }
        else{
            try {
                progressDialog.setMessage("Please wait...");
                progressDialog.setTitle("Adding Student");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                StudentNumbersModels models =new StudentNumbersModels(student_number);
                databaseReference.child(student_number).setValue(models).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            add_student_number.getText().clear();
                            Toast.makeText(getApplicationContext(),"Student number added successfully",Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }catch (Exception n){
                progressDialog.dismiss();
                n.printStackTrace();
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
            }

        }
    }
    /***end**/

    /***Adding a staff member in the database module**/
    public void uploadStaff(){
        String staff_number;

        staff_number = add_staff_number.getText().toString().trim();
        if(staff_number.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please fill all the fields before clicking the button", Toast.LENGTH_SHORT).show();
        }
        else if(staff_number.length()<=4)
        {
            add_staff_number.requestFocus();
            add_staff_number.setError("IT SHOULD CONTAIN 5 DIGITS");

        }
        else{
            try {
                progressDialog.setMessage("Please wait...");
                progressDialog.setTitle("Adding user");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                StaffMemberModel models =new StaffMemberModel(staff_number);
                databaseReferenceStaff.child(staff_number).setValue(models).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            add_staff_number.getText().clear();
                            Toast.makeText(getApplicationContext(),"Staff member added successfully",Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }catch (Exception n){
                progressDialog.dismiss();
                n.printStackTrace();
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
            }

        }
    }
    /***end**/

    /***select book module**/
    public void selectBook(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Choose a book"),13);
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            upload_book.setEnabled(true);
            book_loci.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));
            upload_book.setOnClickListener(v->{
                addBook(data.getData());
            });
        }
    }
    /***Adding a book in the database module**/
    public void addBook(Uri book){

        String book_author,book_title, book_isbn;
        book_author = Book_author.getText().toString().trim();
        book_title = Book_title.getText().toString().trim();
        book_isbn = Book_isbn.getText().toString().trim();
        if(book_isbn.isEmpty()||book_title.isEmpty()||book_author.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please fill all the fields before clicking the button", Toast.LENGTH_SHORT).show();

        }
        else if(book_isbn.length()<=12)
        {
            add_staff_number.requestFocus();
            add_staff_number.setError("IT SHOULD CONTAIN 13 DIGITS");

        }
        else{
            try {
                progressDialog.setMessage("Please wait...");
                progressDialog.setTitle("Adding file");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                databaseReferenceBook = FirebaseDatabase.getInstance().getReference("Books");
                storageReference = FirebaseStorage.getInstance().getReference("Books");
                storageReference.child(book_isbn).putFile(book).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri uri = uriTask.getResult();

                        BookModel bookModel = new BookModel( book_author,book_title,book_isbn,uri.toString(),0);
                        databaseReferenceBook.child(book_isbn).setValue(bookModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AdminPage.this, "Book uploaded successfully", Toast.LENGTH_SHORT).show();
                                    Book_isbn.getText().clear();
                                    Book_title.getText().clear();
                                    Book_author.getText().clear();
                                    book_loci.setText("");
                                      progressDialog.dismiss();
                                }else {
                                     progressDialog.dismiss();
                                    Toast.makeText(AdminPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });

            }catch (Exception v){
                v.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

    /***Removing book module**/
    public void removeBook(){
        String isbn;

        isbn = remove_book.getText().toString().trim();
        if(isbn.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please fill all the fields before clicking the button", Toast.LENGTH_SHORT).show();
        }
        else if(isbn.length()<=12)
        {
            remove_book.requestFocus();
            remove_book.setError("IT SHOULD CONTAIN 13 DIGITS");

        }
        else{
            try {
                progressDialog.setMessage("Please wait...");
                progressDialog.setTitle("Deleting file");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                DatabaseReference books = FirebaseDatabase.getInstance().getReference("Books");
                books.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            BookModel bookModel = snapshot1.getValue(BookModel.class);
                            if(bookModel.getIsbn().equals(isbn)){
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference("Books");

                                StorageReference storageReference = storageRef.child(isbn);


                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        detectNumber = 1;
                                        books.child(isbn).removeValue();
                                        remove_book.getText().clear();
                                        Toast.makeText(AdminPage.this, "Book removed", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(AdminPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        }
                        if(detectNumber!=1){
                            Toast.makeText(AdminPage.this, "This ISBN is not registered", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }catch (Exception n){
                n.printStackTrace();
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        }
    }
    @Override
    public void onBackPressed(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Exit")
                .setMessage("Do you want to exit")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }
                ).show();
    }


}
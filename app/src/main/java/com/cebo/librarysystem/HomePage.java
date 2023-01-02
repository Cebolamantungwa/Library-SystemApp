package com.cebo.librarysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cebo.librarysystem.model.BookModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomePage extends AppCompatActivity {
    private AutoCompleteTextView Search_view;
    private TextView book_alert;
    private ListView Book_list_view;
    private ProgressDialog  progressDialog;
    private AppCompatButton search_btn;

    private ListView listView;


    //book
    private CardView book_card;
    private TextView bookTitle,bookIsbn;
    private TextView View;
    private int bookC = 0;
    //Uri holder
    private String uri;
    //links
    private TextView Info_desk;
    private ImageView Facebook_id,Twitter_id,Instagram_id;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Books");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        book_alert = findViewById(R.id.book_alert);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Books");
        //links
        Info_desk = findViewById(R.id.Info_desk);
        Info_desk.setOnClickListener(v->{
            Info_desk.setMovementMethod(LinkMovementMethod.getInstance());
        });
        //facebook
        Facebook_id = findViewById(R.id.Facebook_id);
        Facebook_id.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://web.facebook.com/UnizuluLibrary/?_rdc=1_rdr"));
            startActivity(intent);
        });
        Twitter_id = findViewById(R.id.Twitter_id);
        Twitter_id.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://twitter.com/UNIZULUongoye"));
            startActivity(intent);
        });
        Instagram_id = findViewById(R.id.Instagram_id);
        Instagram_id.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://www.instagram.com/unizululibraryservices/"));
            startActivity(intent);
        });
        /***end*/
        progressDialog = new ProgressDialog(this);
        //book
        book_card =findViewById(R.id.book_card);
        bookTitle =findViewById(R.id.bookTitle);
        bookIsbn =findViewById(R.id.bookIsbn);
        View = findViewById(R.id.View);
        View.setOnClickListener(v->{
            viewBook();
        });
        //end
        listView = findViewById(R.id.Book_list_view);

        Search_view = findViewById(R.id.Search_view);

        //Auto complete term
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);

        search_btn = findViewById(R.id.search_btn);
        search_btn.setOnClickListener(v->{

            searchBook();
        });
    }

    private void populateSearch(DataSnapshot snapshot) {
           ArrayList<String> books = new ArrayList<>();
           if (snapshot.exists()){
                 for (DataSnapshot ds:snapshot.getChildren()){
                    // BookModel model = ds.getValue(BookModel.class);
                     if(Search_view.getText().equals("[a-zA-Z]+")){
                         String book_name =ds.child("title").getValue(String.class);
                         books.add(book_name);
                     }else {
                         String book_name =ds.child("isbn").getValue(String.class);
                         books.add(book_name);
                     }

                 }
               ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,books);
               Search_view.setAdapter(adapter);
           }else{
               Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
           }
    }

    public  void  searchBook(){
        book_card.setVisibility(View.GONE);
        String book = Search_view.getText().toString();
        if(book.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please fill the field before clicking the button", Toast.LENGTH_SHORT).show();
        }else{
            try {
                progressDialog.setMessage("Searching "+book+" book please wait...");
                progressDialog.setTitle("Search");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                //using author
                Query query_author = databaseReference.orderByChild("author").equalTo(book);
                query_author.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookC = 1;
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                            BookModel Model = dataSnapshot.getValue(BookModel.class);
                            if (Model.getAuthor().equals(book)){
                                book_alert.setVisibility(android.view.View.GONE);
                                book_card.setVisibility(View.VISIBLE);
                                bookIsbn.setText(Model.getIsbn());
                                bookTitle.setText(Model.getTitle());
                                progressDialog.dismiss();

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //using ISBN
                Query query = databaseReference.orderByChild("isbn").equalTo(book);
                query.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookC = 1;
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        BookModel Model = dataSnapshot.getValue(BookModel.class);
                        if (Model.getIsbn().equals(book)){
                            book_alert.setVisibility(android.view.View.GONE);
                            book_card.setVisibility(View.VISIBLE);
                            bookIsbn.setText(Model.getIsbn());
                            bookTitle.setText(Model.getTitle());
                            progressDialog.dismiss();

                        }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //using title
                Query queryTitle = databaseReference.orderByChild("title").equalTo(book);
                queryTitle.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookC =1;
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                            BookModel Model = dataSnapshot.getValue(BookModel.class);
                            if (Model.getTitle().equals(book)){
                                book_alert.setVisibility(android.view.View.GONE);
                                book_card.setVisibility(View.VISIBLE);
                                bookIsbn.setText(Model.getIsbn());
                                bookTitle.setText(Model.getTitle());
                                progressDialog.dismiss();

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomePage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
                if(bookC!=1){
                    book_alert.setVisibility(android.view.View.VISIBLE);
                }
                progressDialog.dismiss();
            }catch (Exception n){
                n.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                 progressDialog.dismiss();
            }
        }
    }
    public void viewBook(){
        try {
            progressDialog.setMessage("Viewing please wait...");
            progressDialog.setTitle("View");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            Query query = databaseReference.orderByChild("isbn").equalTo(Search_view.getText().toString());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        BookModel Model = dataSnapshot.getValue(BookModel.class);
                        if (Model.getIsbn().equals(Search_view.getText().toString())){
                            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Books").child(Model.getIsbn());
                            HashMap hashMap = new HashMap();
                            hashMap.put("view_count",Model.getView_count()+1);

                            firebaseDatabase.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    uri = Model.getUri();
                                    Intent intent = new Intent(HomePage.this,PDFViewer.class);
                                    intent.putExtra("Uri",uri);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                }
                            });

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                }
            });
            Query queryT = databaseReference.orderByChild("title").equalTo(Search_view.getText().toString());
            queryT.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        BookModel Model = dataSnapshot.getValue(BookModel.class);
                        if (Model.getTitle().equals(Search_view.getText().toString())){
                            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Books").child(Model.getIsbn());
                            HashMap hashMap = new HashMap();
                            hashMap.put("view_count",Model.getView_count()+1);

                            firebaseDatabase.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    uri = Model.getUri();
                                    Intent intent = new Intent(HomePage.this,PDFViewer.class);
                                    intent.putExtra("Uri",uri);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                }
            });

        }catch (Exception n){
            progressDialog.dismiss();
            n.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onBackPressed(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Exit")
                .setMessage("Do you want to exit application")
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
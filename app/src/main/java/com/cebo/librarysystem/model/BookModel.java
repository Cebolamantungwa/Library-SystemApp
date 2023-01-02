package com.cebo.librarysystem.model;

public class BookModel {
    String author,title, isbn,uri;
    int view_count;
    public BookModel() {

    }

    public BookModel(String author, String title, String isbn, String uri, int view_count) {
        this.author = author;
        this.title = title;
        this.isbn = isbn;
        this.uri = uri;
        this.view_count = view_count;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }
}

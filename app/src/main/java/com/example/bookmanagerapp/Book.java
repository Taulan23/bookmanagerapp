
package com.example.bookmanagerapp;

public class Book {
    private int id;
    private String title;
    private String author;
    private String dateFrom;
    private String dateTo;
    private boolean isRead;

    public Book(int id, String title, String author, String dateFrom, String dateTo, boolean isRead) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.isRead = isRead;
    }

    public Book(String title, String author, String dateFrom, String dateTo, boolean isRead) {
        this(-1, title, author, dateFrom, dateTo, isRead);
    }

    public Book(String title, String author, boolean b) {
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return title + " - " + author;
    }
}

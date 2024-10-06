// Имя файла: BookDatabaseHelper.java
// Путь: app/src/main/java/com/example/bookmanagerapp/BookDatabaseHelper.java

package com.example.bookmanagerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class BookDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "library.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_BOOKS = "books";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_DATE_FROM = "date_from";
    private static final String COLUMN_DATE_TO = "date_to";
    private static final String COLUMN_IS_READ = "is_read";

    public BookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_BOOKS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_AUTHOR + " TEXT, "
                + COLUMN_DATE_FROM + " TEXT, "
                + COLUMN_DATE_TO + " TEXT, "
                + COLUMN_IS_READ + " INTEGER DEFAULT 0"
                + ");";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }

    public void addBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_AUTHOR, book.getAuthor());
        values.put(COLUMN_DATE_FROM, book.getDateFrom());
        values.put(COLUMN_DATE_TO, book.getDateTo());
        values.put(COLUMN_IS_READ, book.isRead() ? 1 : 0);

        db.insert(TABLE_BOOKS, null, values);
        db.close();
    }

    public Book findBookByTitle(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, null, COLUMN_TITLE + "=?", new String[]{title}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Book book = new Book(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DATE_FROM)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TO)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_IS_READ)) == 1
            );
            cursor.close();
            return book;
        } else {
            return null;
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKS, null);
        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE_FROM)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TO)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_IS_READ)) == 1
                );
                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return books;
    }

    public List<Book> getReadBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_IS_READ + "=1", null);
        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE_FROM)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TO)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_IS_READ)) == 1
                );
                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return books;
    }

    public void updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_AUTHOR, book.getAuthor());
        values.put(COLUMN_DATE_FROM, book.getDateFrom());
        values.put(COLUMN_DATE_TO, book.getDateTo());
        values.put(COLUMN_IS_READ, book.isRead() ? 1 : 0);

        db.update(TABLE_BOOKS, values, COLUMN_ID + "=?", new String[]{String.valueOf(book.getId())});
        db.close();
    }

    public void deleteBook(int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKS, COLUMN_ID + "=?", new String[]{String.valueOf(bookId)});
        db.close();
    }

    // Метод для поиска книг по названию и автору
    public List<Book> searchBooksByTitleAndAuthor(String title, String author) {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "";
        List<String> selectionArgsList = new ArrayList<>();

        if (!title.isEmpty()) {
            selection += COLUMN_TITLE + " LIKE ?";
            selectionArgsList.add("%" + title + "%");
        }

        if (!author.isEmpty()) {
            if (!selection.isEmpty()) {
                selection += " AND ";
            }
            selection += COLUMN_AUTHOR + " LIKE ?";
            selectionArgsList.add("%" + author + "%");
        }

        String[] selectionArgs = selectionArgsList.toArray(new String[0]);
        Cursor cursor = db.query(TABLE_BOOKS, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE_FROM)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TO)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_IS_READ)) == 1
                );
                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return books;
    }
}

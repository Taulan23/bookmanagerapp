package com.example.bookmanagerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText bookTitleEditText;
    private EditText bookAuthorEditText;
    private EditText dateFromEditText;
    private EditText dateToEditText;
    private EditText searchTitleEditText;
    private EditText searchAuthorEditText;
    private Button addBookButton;
    private Button searchButton;
    private LinearLayout bookListContainer;
    private BookDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new BookDatabaseHelper(this);

        // Инициализация виджетов
        bookTitleEditText = findViewById(R.id.book_title_edit_text);
        bookAuthorEditText = findViewById(R.id.book_author_edit_text);
        dateFromEditText = findViewById(R.id.date_from_edit_text);
        dateToEditText = findViewById(R.id.date_to_edit_text);
        searchTitleEditText = findViewById(R.id.search_title_edit_text);
        searchAuthorEditText = findViewById(R.id.search_author_edit_text);
        addBookButton = findViewById(R.id.add_book_button);
        searchButton = findViewById(R.id.search_button);
        bookListContainer = findViewById(R.id.book_list_container);

        // Настройка выбора даты
        dateFromEditText.setOnClickListener(v -> showDatePickerDialog(dateFromEditText));
        dateToEditText.setOnClickListener(v -> showDatePickerDialog(dateToEditText));

        // Добавление книги в базу данных
        addBookButton.setOnClickListener(v -> {
            String title = bookTitleEditText.getText().toString().trim();
            String author = bookAuthorEditText.getText().toString().trim();
            String dateFrom = dateFromEditText.getText().toString().trim();
            String dateTo = dateToEditText.getText().toString().trim();

            if (!title.isEmpty() && !author.isEmpty() && !dateFrom.isEmpty() && !dateTo.isEmpty()) {
                Book book = new Book(title, author, dateFrom, dateTo, false);
                dbHelper.addBook(book);
                Toast.makeText(MainActivity.this, "Книга добавлена", Toast.LENGTH_SHORT).show();
                loadBooks();
                clearFields();
            } else {
                Toast.makeText(MainActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            }
        });

        // Поиск книг по названию и автору
        searchButton.setOnClickListener(v -> {
            String titleQuery = searchTitleEditText.getText().toString().trim();
            String authorQuery = searchAuthorEditText.getText().toString().trim();

            List<Book> foundBooks;
            if (!titleQuery.isEmpty() || !authorQuery.isEmpty()) {
                foundBooks = dbHelper.searchBooksByTitleAndAuthor(titleQuery, authorQuery);
                displayBooks(foundBooks);
            } else {
                loadBooks();  // Показать все книги, если поля поиска пустые
            }
        });

        // Загрузка всех книг при запуске приложения
        loadBooks();
    }

    // Метод для отображения диалога выбора даты
    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editText.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Загрузка всех книг из базы данных
    private void loadBooks() {
        List<Book> books = dbHelper.getAllBooks();
        displayBooks(books);
    }

    // Метод для отображения книг в контейнере
    private void displayBooks(List<Book> books) {
        // Очищаем контейнер перед добавлением новых элементов
        bookListContainer.removeAllViews();

        // Проходим по списку книг и добавляем каждый элемент в контейнер
        for (Book book : books) {
            // Используем созданный макет book_list_item для каждого элемента списка
            View bookView = LayoutInflater.from(this).inflate(R.layout.book_list_item, bookListContainer, false);

            TextView bookTitleTextView = bookView.findViewById(R.id.book_title_text_view);
            TextView bookAuthorTextView = bookView.findViewById(R.id.book_author_text_view);
            TextView dateFromTextView = bookView.findViewById(R.id.date_from_text_view);
            TextView dateToTextView = bookView.findViewById(R.id.date_to_text_view);
            CheckBox readCheckBox = bookView.findViewById(R.id.read_checkbox);
            Button deleteButton = bookView.findViewById(R.id.delete_button);
            Button reportButton = bookView.findViewById(R.id.report_button); // Кнопка для отправки отчета

            // Установка значений для элементов
            bookTitleTextView.setText("Название: " + book.getTitle());
            bookAuthorTextView.setText("Автор: " + book.getAuthor());
            dateFromTextView.setText("Начало чтения: " + book.getDateFrom());
            dateToTextView.setText("Конец чтения: " + book.getDateTo());
            readCheckBox.setChecked(book.isRead());

            // Обработка изменения статуса "Прочитано"
            readCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                book.setRead(isChecked);
                dbHelper.updateBook(book);
                Toast.makeText(MainActivity.this, "Статус книги обновлен", Toast.LENGTH_SHORT).show();
            });

            // Удаление книги
            deleteButton.setOnClickListener(v -> {
                dbHelper.deleteBook(book.getId());
                Toast.makeText(MainActivity.this, "Книга удалена", Toast.LENGTH_SHORT).show();
                loadBooks();
            });

            // Отправка отчета о прочтении книги
            reportButton.setOnClickListener(v -> {
                sendReport(book);
            });

            // Добавляем элемент книги в контейнер
            bookListContainer.addView(bookView);
        }
    }

    // Метод для отправки отчета о прочтении книги
    private void sendReport(Book book) {
        String subject = "Отчет о прочтении книги";
        String message = "Название книги: " + book.getTitle() + "\n" +
                "Автор: " + book.getAuthor() + "\n" +
                "Дата начала чтения: " + book.getDateFrom() + "\n" +
                "Дата окончания чтения: " + book.getDateTo() + "\n" +
                "Статус: " + (book.isRead() ? "Прочитано" : "Не прочитано");

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Отправить отчет через..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "Нет установленных почтовых клиентов.", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для очистки полей ввода после добавления книги
    private void clearFields() {
        bookTitleEditText.setText("");
        bookAuthorEditText.setText("");
        dateFromEditText.setText("");
        dateToEditText.setText("");
    }
}

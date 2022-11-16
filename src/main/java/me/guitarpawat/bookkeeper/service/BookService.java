package me.guitarpawat.bookkeeper.service;

import me.guitarpawat.bookkeeper.entity.Book;
import me.guitarpawat.bookkeeper.enums.BookUpsertMode;
import me.guitarpawat.bookkeeper.model.AddBookResponse;

import java.util.List;

public interface BookService {
    Book getBook(String id);

    List<Book> listBookByType(String status, int top, String fromId);

    AddBookResponse upsertBook(Book book, BookUpsertMode mode);
}

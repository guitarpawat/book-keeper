package me.guitarpawat.bookkeeper.service;

import me.guitarpawat.bookkeeper.entity.Book;
import me.guitarpawat.bookkeeper.enums.BookStatus;
import me.guitarpawat.bookkeeper.enums.BookUpsertMode;
import me.guitarpawat.bookkeeper.model.AddBookResponse;
import me.guitarpawat.bookkeeper.repository.BooksRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BooksRepository booksRepository;

    @Override
    public Book getBook(String id) {
        Optional<Book> bookOptional = booksRepository.findById(id);
        if (bookOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book id " + id + " not found");
        }
        return bookOptional.get();
    }

    @Override
    public List<Book> listBookByType(String status, int top, String fromId) {
        BookStatus bookStatus;
        try {
            bookStatus = BookStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid status", e);
        }

        if (StringUtils.isNotEmpty(fromId) && !ObjectId.isValid(fromId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid format for fromId");
        }

        Pageable pageable = Pageable.ofSize(top);
        List<Book> books;
        if (StringUtils.isEmpty(fromId)) {
            books = booksRepository.findByStatusOrderByIdDesc(bookStatus, pageable);
        } else {
            books = booksRepository.findByStatusAndIdGreaterThanOrderByIdDesc(bookStatus, fromId, pageable);
        }

        return books;
    }

    @Override
    public AddBookResponse upsertBook(Book book, BookUpsertMode mode) {
        if (BookUpsertMode.INSERT.equals(mode) && StringUtils.isNotEmpty(book.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "custom object id is not supported");
        }

        if (BookUpsertMode.UPDATE.equals(mode) && !booksRepository.existsById(book.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book id " + book.getId() + " not found");
        }

        book = booksRepository.save(book);

        AddBookResponse response = new AddBookResponse();
        response.setId(book.getId());
        return response;
    }


}

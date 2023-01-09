package me.guitarpawat.bookkeeper.service;

import me.guitarpawat.bookkeeper.entity.Book;
import me.guitarpawat.bookkeeper.enums.BookStatus;
import me.guitarpawat.bookkeeper.enums.BookUpsertMode;
import me.guitarpawat.bookkeeper.model.AddBookResponse;
import me.guitarpawat.bookkeeper.repository.BooksRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BooksRepository booksRepository;

    String bookId = ObjectId.get().toHexString();

    @Test
    void getBook_error_notFound() {
        doReturn(Optional.empty()).when(booksRepository).findById(bookId);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> bookService.getBook(bookId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
    @Test
    void getBook_success() {
        Book book = new Book();
        book.setId(bookId);
        doReturn(Optional.of(book)).when(booksRepository).findById(bookId);

        Book result = bookService.getBook(bookId);
        assertEquals(book, result);
    }

    @Test
    void listBookByType_error_invalidStatus() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> bookService.listBookByType("invalid", 10, null));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void listBookByType_error_invalidObjectId() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> bookService.listBookByType("WISHLIST", 10, "invalid"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void listBookByType_success_withFromId() {
        Book book = new Book();
        book.setId(bookId);
        List<Book> bookList = List.of(book);
        doReturn(bookList).when(booksRepository).findByStatusAndIdGreaterThanOrderByIdDesc(eq(BookStatus.BOOKSHELF), eq(bookId), any(Pageable.class));

        List<Book> result = bookService.listBookByType("bookshelf", 10, bookId);
        assertEquals(bookList, result);
    }

    @Test
    void listBookByType_success_withoutFromId() {
        Book book = new Book();
        book.setId(bookId);
        List<Book> bookList = List.of(book);
        doReturn(bookList).when(booksRepository).findByStatusOrderByIdDesc(eq(BookStatus.BOOKSHELF), any(Pageable.class));

        List<Book> result = bookService.listBookByType("bookshelf", 10, null);
        assertEquals(bookList, result);
    }

    @Test
    void upsertBook_success_upsertMode() {
        Book book = new Book();
        book.setId(bookId);
        doReturn(book).when(booksRepository).save(book);

        AddBookResponse response = bookService.upsertBook(book, BookUpsertMode.UPSERT);
        assertEquals(bookId, response.getId());

        verify(booksRepository, never()).existsById(any());
    }

    @Test
    void upsertBook_success_insertMode() {
        Book book = new Book();
        Book expected = new Book();
        expected.setId(bookId);
        doReturn(expected).when(booksRepository).save(book);

        AddBookResponse response = bookService.upsertBook(book, BookUpsertMode.INSERT);
        assertEquals(expected.getId(), response.getId());

        verify(booksRepository, never()).existsById(any());
    }

    @Test
    void upsertBook_error_insertMode_withObjectId() {
        Book book = new Book();
        book.setId(bookId);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> bookService.upsertBook(book, BookUpsertMode.INSERT));
        assertEquals(ex.getStatus(), HttpStatus.BAD_REQUEST);

        verify(booksRepository, never()).save(any());
        verify(booksRepository, never()).existsById(any());
    }

    @Test
    void upsertBook_success_updateMode() {
        Book book = new Book();
        book.setId(bookId);
        doReturn(book).when(booksRepository).save(book);
        doReturn(true).when(booksRepository).existsById(bookId);

        AddBookResponse response = bookService.upsertBook(book, BookUpsertMode.UPDATE);
        assertEquals(bookId, response.getId());
    }

    @Test
    void upsertBook_error_updateMode_notFound() {
        Book book = new Book();
        book.setId(bookId);
        doReturn(false).when(booksRepository).existsById(bookId);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> bookService.upsertBook(book, BookUpsertMode.UPDATE));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());

        verify(booksRepository, never()).save(any());
    }
}
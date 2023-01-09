package me.guitarpawat.bookkeeper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.guitarpawat.bookkeeper.entity.Book;
import me.guitarpawat.bookkeeper.enums.BookStatus;
import me.guitarpawat.bookkeeper.enums.BookType;
import me.guitarpawat.bookkeeper.enums.BookUpsertMode;
import me.guitarpawat.bookkeeper.model.AddBookResponse;
import me.guitarpawat.bookkeeper.service.BookService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class BookControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    String bookId = ObjectId.get().toHexString();

    @Test
    void getBookInfo_success() throws Exception {
        Book book = new Book();
        book.setId(bookId);
        doReturn(book).when(bookService).getBook(bookId);

        mvc.perform(get("/api/v1/books").param("id", bookId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(bookId));
    }

    @Test
    void getBookInfo_error_noId() throws Exception {
        mvc.perform(get("/api/v1/books"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookService);
    }

    @Test
    void listBookByStatus_success() throws Exception {
        Book book = new Book();
        book.setId(bookId);
        doReturn(List.of(book)).when(bookService).listBookByType("statusParam", 10, bookId);

        mvc.perform(get("/api/v1/books/statusParam")
                        .param("top", "10")
                        .param("fromId", bookId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookId));
    }

    @Test
    void listBookByStatus_error_invalidTop() throws Exception {
        mvc.perform(get("/api/v1/books/statusParam")
                        .param("top", "101")
                        .param("fromId", bookId))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookService);
    }

    @Test
    void addBook_success() throws Exception {
        Book book = new Book();
        book.setName("name");
        book.setType(BookType.E_BOOK);
        book.setStatus(BookStatus.BOOKSHELF);

        AddBookResponse response = new AddBookResponse();
        response.setId(bookId);

        doReturn(response).when(bookService).upsertBook(book, BookUpsertMode.INSERT);

        mvc.perform(post("/api/v1/books")
                        .content(objectMapper.writeValueAsString(book))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(bookId));
    }

    @Test
    void addBook_error_missing_name() throws Exception {
        Book book = new Book();
        book.setType(BookType.E_BOOK);
        book.setStatus(BookStatus.BOOKSHELF);

        mvc.perform(post("/api/v1/books")
                        .content(objectMapper.writeValueAsString(book))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookService);
    }

    @Test
    void editBook_success() throws Exception {
        Book book = new Book();
        book.setName("name");
        book.setType(BookType.E_BOOK);
        book.setStatus(BookStatus.BOOKSHELF);

        AddBookResponse response = new AddBookResponse();
        response.setId(bookId);

        doReturn(response).when(bookService).upsertBook(book, BookUpsertMode.UPDATE);

        mvc.perform(put("/api/v1/books")
                        .content(objectMapper.writeValueAsString(book))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void editBook_error_missing_name() throws Exception {
        Book book = new Book();
        book.setType(BookType.E_BOOK);
        book.setStatus(BookStatus.BOOKSHELF);

        mvc.perform(put("/api/v1/books")
                        .content(objectMapper.writeValueAsString(book))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookService);
    }
}
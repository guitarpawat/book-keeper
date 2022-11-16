package me.guitarpawat.bookkeeper.controller;

import me.guitarpawat.bookkeeper.entity.Book;
import me.guitarpawat.bookkeeper.enums.BookUpsertMode;
import me.guitarpawat.bookkeeper.model.AddBookResponse;
import me.guitarpawat.bookkeeper.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/api/v1/books")
    public ResponseEntity<Book> getBookInfo(@RequestParam String id) {
        return ResponseEntity.ok(bookService.getBook(id));
    }

    @GetMapping("/api/v1/books/{status}")
    public ResponseEntity<List<Book>> listBookByStatus(
            @PathVariable String status,
            @RequestParam @Min(1) @Max(100) int top,
            @RequestParam(required = false) String fromId) {
        return ResponseEntity.ok(bookService.listBookByType(status, top, fromId));
    }

    @PostMapping("/api/v1/books")
    public ResponseEntity<AddBookResponse> addBook(@RequestBody @Valid Book book) {
        return ResponseEntity.ok(bookService.upsertBook(book, BookUpsertMode.INSERT));
    }

    @PutMapping("/api/v1/books")
    public ResponseEntity<Map<String, Object>> editBook(@RequestBody @Valid Book book) {
        bookService.upsertBook(book, BookUpsertMode.UPDATE);
        return ResponseEntity.ok(Map.of());
    }
}

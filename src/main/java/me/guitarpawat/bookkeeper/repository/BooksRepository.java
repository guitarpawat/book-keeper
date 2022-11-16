package me.guitarpawat.bookkeeper.repository;

import me.guitarpawat.bookkeeper.entity.Book;
import me.guitarpawat.bookkeeper.enums.BookStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BooksRepository extends MongoRepository<Book, String> {

    List<Book> findByStatusAndIdGreaterThanOrderByIdDesc(BookStatus status, String id, Pageable pageable);

    List<Book> findByStatusOrderByIdDesc(BookStatus status, Pageable pageable);
}

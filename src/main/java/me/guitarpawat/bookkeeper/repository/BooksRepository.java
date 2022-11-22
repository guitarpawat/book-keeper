package me.guitarpawat.bookkeeper.repository;

import me.guitarpawat.bookkeeper.entity.Book;
import me.guitarpawat.bookkeeper.enums.BookStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends MongoRepository<Book, String> {

    List<Book> findByStatusAndIdGreaterThanOrderByIdDesc(BookStatus status, String id, Pageable pageable);

    List<Book> findByStatusOrderByIdDesc(BookStatus status, Pageable pageable);
}

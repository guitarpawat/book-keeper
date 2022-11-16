package me.guitarpawat.bookkeeper.entity;

import com.mongodb.lang.NonNull;
import lombok.Data;
import me.guitarpawat.bookkeeper.enums.BookStatus;
import me.guitarpawat.bookkeeper.enums.BookType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Document("books")
@Data
public class Book {

    @Id
    private String id;

    @NonNull
    private String name;

    private List<String> authors;

    @NonNull
    private BookStatus status;

    @NonNull
    private BookType type;

    private String edition;

    private String isbn;

    private List<String> tags;

    @Min(0)
    @Max(10)
    private Integer rating;
}

package api.books.booksapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Embeddable
public class BookTagId implements Serializable {

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "tag_id", length = 50)
    private String tagId;

    public BookTagId(Long bookId, String tagId) {
        this.bookId = bookId;
        this.tagId = tagId;
    }
}
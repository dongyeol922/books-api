package api.books.booksapi.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @Column(name = "tag_id", length = 50)
    private String tagId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
}

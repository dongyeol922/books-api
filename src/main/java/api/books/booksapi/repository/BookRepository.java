package api.books.booksapi.repository;

import api.books.booksapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b JOIN b.bookTags bt WHERE bt.tag.tagId = :tagId")
    List<Book> findTop5ByTagId(@Param("tagId") String tagId, org.springframework.data.domain.Pageable pageable);

    @Query(value = "SELECT * FROM books ORDER BY RANDOM() LIMIT 5", nativeQuery = true)
    List<Book> findTop5Random();
}
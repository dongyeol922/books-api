package api.books.booksapi.repository;

import api.books.booksapi.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b JOIN b.bookTags bt WHERE bt.tag.tagId = :tagId")
    List<Book> findTop5ByTagId(@Param("tagId") String tagId, Pageable pageable);

    @Query(value = "SELECT * FROM books ORDER BY RANDOM() LIMIT 5", nativeQuery = true)
    List<Book> findTop5Random();

    List<Book> findByCategoryCategoryId(String categoryId, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.category.categoryId = :categoryId " +
           "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findByCategoryAndTitle(@Param("categoryId") String categoryId,
                                      @Param("title") String title,
                                      Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.bookTags bt WHERE bt.tag.tagId = :tagId " +
           "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findByTagAndTitle(@Param("tagId") String tagId,
                                 @Param("title") String title,
                                 Pageable pageable);
}
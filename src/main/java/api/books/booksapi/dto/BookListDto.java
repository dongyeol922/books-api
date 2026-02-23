package api.books.booksapi.dto;

import api.books.booksapi.entity.Book;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class BookListDto {

    @Getter
    @Setter
    public static class BookSearchDTO {
        private String category;
        private String title;
        private int page = 0;
        private int size = 10;
    }

    @Getter
    public static class BookItem {
        private final Long bookId;
        private final String title;
        private final String author;
        private final String images;
        private final String description;

        private BookItem(Long bookId, String title, String author, String images, String description) {
            this.bookId = bookId;
            this.title = title;
            this.author = author;
            this.images = images;
            this.description = description;
        }

        public static BookItem from(Book book) {
            return new BookItem(
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getImageUrl(),
                    book.getDescription()
            );
        }
    }

    @Getter
    public static class BookListResponse {
        private final String category;
        private final List<BookItem> books;

        public BookListResponse(String category, List<BookItem> books) {
            this.category = category;
            this.books = books;
        }
    }
}
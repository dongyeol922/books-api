package api.books.booksapi.dto;

import api.books.booksapi.entity.Book;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

public class MainDto {

    @Getter
    public static class BookItem {
        private final Long id;
        private final String title;
        private final String author;
        private final String images;

        private BookItem(Long id, String title, String author, String images) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.images = images;
        }

        public static BookItem from(Book book) {
            return new BookItem(
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getImageUrl()
            );
        }
    }

    @Getter
    public static class MainResponse {
        private final List<BookItem> bestseller;

        @JsonProperty("new")
        private final List<BookItem> newBooks;
        private final List<BookItem> monthly;

        public MainResponse(List<BookItem> bestseller, List<BookItem> newBooks, List<BookItem> monthly) {
            this.bestseller = bestseller;
            this.newBooks = newBooks;
            this.monthly = monthly;
        }
    }
}
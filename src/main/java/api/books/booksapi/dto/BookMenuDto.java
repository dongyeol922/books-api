package api.books.booksapi.dto;

import lombok.Getter;
import lombok.Setter;

public class BookMenuDto {

    @Getter
    @Setter
    public static class BookSearchDTO {
        private String types;
        private String title;
        private int page = 0;
        private int size = 10;
    }
}

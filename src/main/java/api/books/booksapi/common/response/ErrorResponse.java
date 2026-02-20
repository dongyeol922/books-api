package api.books.booksapi.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final int code;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime date;

    private final String message;

    private ErrorResponse(int code, String message) {
        this.code = code;
        this.date = LocalDateTime.now();
        this.message = message;
    }

    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(code, message);
    }
}
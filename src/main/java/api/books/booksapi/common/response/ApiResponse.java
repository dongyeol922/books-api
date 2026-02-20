package api.books.booksapi.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {

    private final int code;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime date;

    private final T content;

    private ApiResponse(int code, T content) {
        this.code = code;
        this.date = LocalDateTime.now();
        this.content = content;
    }

    public static <T> ApiResponse<T> ok(T content) {
        return new ApiResponse<>(200, content);
    }
}

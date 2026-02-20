package api.books.booksapi.controller;

import api.books.booksapi.common.response.ApiResponse;
import api.books.booksapi.dto.MainDto;
import api.books.booksapi.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/main")
    public ResponseEntity<ApiResponse<MainDto.MainResponse>> getMainPage() {
        MainDto.MainResponse data = bookService.getMainPageData();
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
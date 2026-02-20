package api.books.booksapi.service;

import api.books.booksapi.dto.MainDto;
import api.books.booksapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private static final String TAG_BESTSELLER = "BESTSELLER";
    private static final String TAG_NEW = "NEW";
    private static final int TOP_SIZE = 5;

    private final BookRepository bookRepository;

    public MainDto.MainResponse getMainPageData() {
        PageRequest pageRequest = PageRequest.of(0, TOP_SIZE);

        List<MainDto.BookItem> bestseller = bookRepository
                .findTop5ByTagId(TAG_BESTSELLER, pageRequest)
                .stream()
                .map(MainDto.BookItem::from)
                .toList();

        List<MainDto.BookItem> newBooks = bookRepository
                .findTop5ByTagId(TAG_NEW, pageRequest)
                .stream()
                .map(MainDto.BookItem::from)
                .toList();

        List<MainDto.BookItem> monthly = bookRepository
                .findTop5Random()
                .stream()
                .map(MainDto.BookItem::from)
                .toList();

        return new MainDto.MainResponse(bestseller, newBooks, monthly);
    }
}
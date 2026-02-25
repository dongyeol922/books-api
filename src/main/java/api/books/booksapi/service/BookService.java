package api.books.booksapi.service;

import api.books.booksapi.dto.BookListDto;
import api.books.booksapi.dto.BookMenuDto;
import api.books.booksapi.dto.MainDto;
import api.books.booksapi.entity.Category;
import api.books.booksapi.repository.BookRepository;
import api.books.booksapi.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

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

    public BookListDto.BookListResponse getBooksByCategory(BookListDto.BookSearchDTO searchDTO) {
        if (searchDTO.getCategory() == null || searchDTO.getCategory().isBlank()) {
            throw new IllegalArgumentException("카테고리는 필수 입력값입니다.");
        }

        Category category = categoryRepository.findById(searchDTO.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + searchDTO.getCategory()));

        String title = (searchDTO.getTitle() == null || searchDTO.getTitle().isBlank())
                ? null
                : searchDTO.getTitle().trim();

        PageRequest pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize());

        long total = (title == null)
                ? bookRepository.countByCategoryCategoryId(category.getCategoryId())
                : bookRepository.countByCategoryAndTitle(category.getCategoryId(), title);

        List<BookListDto.BookItem> books = (title == null)
                ? bookRepository.findByCategoryCategoryId(category.getCategoryId(), pageable)
                        .stream().map(BookListDto.BookItem::from).toList()
                : bookRepository.findByCategoryAndTitle(category.getCategoryId(), title, pageable)
                        .stream().map(BookListDto.BookItem::from).toList();

        return new BookListDto.BookListResponse(category.getCategoryName(), books, searchDTO.getPage(), total);
    }

    public BookListDto.BookListResponse getBooksByMenu(BookMenuDto.BookSearchDTO searchDTO) {
        if (searchDTO.getTypes() == null || searchDTO.getTypes().isBlank()) {
            throw new IllegalArgumentException("타입은 필수 입력값입니다.");
        }

        String types = searchDTO.getTypes().trim();
        String title = (searchDTO.getTitle() == null || searchDTO.getTitle().isBlank())
                ? null
                : searchDTO.getTitle().trim();
        PageRequest pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize());

        // NEW, BESTSELLER → 태그 기반 조회
        if (TAG_NEW.equals(types) || TAG_BESTSELLER.equals(types)) {
            String tagId = TAG_NEW.equals(types) ? TAG_NEW : TAG_BESTSELLER;

            long total = (title == null)
                    ? bookRepository.countByTagId(tagId)
                    : bookRepository.countByTagAndTitle(tagId, title);

            List<BookListDto.BookItem> books = (title == null)
                    ? bookRepository.findTop5ByTagId(tagId, pageable)
                            .stream().map(BookListDto.BookItem::from).toList()
                    : bookRepository.findByTagAndTitle(tagId, title, pageable)
                            .stream().map(BookListDto.BookItem::from).toList();

            return new BookListDto.BookListResponse(types, books, searchDTO.getPage(), total);
        }

        // 그 외 (IT, NOVEL, ESSAY 등) → 카테고리 기반 조회 (기존 로직 재사용)
        BookListDto.BookSearchDTO categorySearch = new BookListDto.BookSearchDTO();
        categorySearch.setCategory(types);
        categorySearch.setTitle(searchDTO.getTitle());
        categorySearch.setPage(searchDTO.getPage());
        categorySearch.setSize(searchDTO.getSize());
        return getBooksByCategory(categorySearch);
    }
}
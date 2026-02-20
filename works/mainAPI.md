서점 메인화면 api 만들기
## 1. Response Format 
```
{
  "bestseller": [
    { "id": "b1", "title": "불편한 편의점 2", "author": "김호연", "images": "https://picsum.photos/seed/b1/400/560" }
  ],
  "new": [
    { "id": "n1", "title": "하얼빈", "author": "김훈", "images": "https://picsum.photos/seed/n1/400/560" }
  ],
  "monthly": [
    { "id": "m1", "title": "작별하지 않는다", "author": "한강", "images": "https://picsum.photos/seed/m1/400/560" }
  ]
}
```
## 2. Database 
- /script/bookstore_init.sql  파일을 참고 

## 3. 데이터 처리
- 데이터 format 에서 bestseller 는  베스트셀러 top5
- 데이터 format 에서 new 는  신간 top5
- 데이터 format 에서 monthly 는 추천도서 top5
- 추천도서는 책 list 에서 랜덤으로 5개 추출 

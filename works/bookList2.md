카테고리 별 책 리스트 API

## 1. 목적
- client에서 요청하는 메뉴 도서 리스트 api 구현 

## 2. 메뉴
- 새로운책 , 베스트셀러,소설 ,IT ,에세이, 자기개발 
- 해당 메뉴에 대한 도서 리스트를 api 로 제공
- 소설 ,IT ,에세이, 자기개발 카테고리 별 도서
- 새로운책 , 베스트셀러 는 비 카테고리 도서
- 카테고리별 도서는 /api/books/list 를 이용하면 되는지 분석 


## 2. 구현
- 페이징 처리 가능
- 한페이지에 10개씩 보여주기 
- 도서 이름으로 검색 가능

## 3. 매개변수 
- types : 도서타입(새로운책 , 베스트셀러,소설 ,IT ,에세이, 자기개발)
- title : 도서제목 (옵션)
- page : 보여줄 페이지(옵션), default 0
- size : 한페이지에 보여줄 개수(옵션), default 10
- 매개변수는 DTO 만들어서 처리, 이름은 BookSearchDTO

##4. 통신 방식
- front-end : axios 사용하여 호출
- back-end  : api 형식
- method : get


## 5.데이터 
```
 {
   "category" :"IT",
   "books" :[
      {
         "bookId" : 1,
         "title" :  "자바의 정석",
         "author" : "남궁성",
         "images: : "/api/books/image/jungsuk.png",
         "description" :"자바의 정석은..."
      }
   ]
 }
```
# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

온라인 서점 e-commerce API. Spring Boot 3.5.10 + Java 21 + PostgreSQL 기반.
현재 프로젝트 스켈레톤과 DB 스키마(`script/bookstore_init.sql`)만 구성된 초기 단계.

## 아키텍처
**패키지 루트:** `api.books.booksapi`

Spring Boot 레이어드 아키텍처 기반:
- Controller → Service → Repository → Entity → PostgreSQL

**DB 도메인 구조** (`script/bookstore_init.sql` 참고):
- 회원(members, roles, member_addresses)
- 도서(books, categories, tags, book_tags)
- 장바구니(cart_items)
- 주문/결제(orders, order_items, payments)
- 리뷰(reviews)

주문 상태 흐름: ORDERED → PAID → SHIPPING → DELIVERED / CANCELLED
결제 상태 흐름: PENDING → COMPLETED / REFUNDED / FAILED

## 주요 설정

- **서버 포트:** 9090 (`application.yml`)
- **DB:** PostgreSQL (`jdbc:postgresql://222.239.249.197:5432/my_db`)
- **JPA:** DDL auto=none (수동 스키마 관리), show-sql=true, open-in-view=false
- **Lombok:** 사용 중 (compileOnly + annotationProcessor)
- **Spring Boot DevTools:** 개발 시 자동 리로드 활성화

## 의존성

- `spring-boot-starter-data-jpa` — ORM 및 Repository 패턴
- `spring-boot-starter-web` — REST API
- `postgresql` — PostgreSQL JDBC 드라이버
- `lombok` — 보일러플레이트 코드 축소
- `spring-boot-starter-test` + JUnit 5 — 테스트

## DB 스키마 주요 제약조건

- `cart_items`: (member_id, book_id) 유니크 — 동일 도서 중복 불가
- `reviews`: order_item_id 유니크 — 구매 건당 리뷰 1개
- `reviews.rating`: 1~5 범위 CHECK 제약
- `books.price`, `books.sale_price`: 양수 CHECK 제약
- 스키마 변경 시 `script/bookstore_init.sql`을 직접 수정 (JPA DDL auto=none)
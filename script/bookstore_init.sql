-- ============================================
-- 도서 판매 사이트 데이터베이스 DDL
-- DBMS: PostgreSQL
-- 생성일: 2026-02-11
-- ============================================

-- ============================================
-- 1. 회원 도메인
-- ============================================

-- 권한
CREATE TABLE roles (
    role_id     VARCHAR(30)       PRIMARY KEY,
    role_name   VARCHAR(30)     NOT NULL UNIQUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  roles                IS '권한';
COMMENT ON COLUMN roles.role_id         IS '(CUSTOMER, ADMIN 등)';
COMMENT ON COLUMN roles.role_name      IS '권한명(손님, 관리자 등) ';


-- 회원
CREATE TABLE members (
    member_id   BIGSERIAL       PRIMARY KEY,
    email       VARCHAR(100)    NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    mem_name    VARCHAR(50)     NOT NULL,
    mobile      VARCHAR(20),
    role_id     VARCHAR(30)      NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_member_role
        FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

CREATE INDEX idx_member_role ON members (role_id);

COMMENT ON TABLE  members              IS '회원';
COMMENT ON COLUMN members.member_id    IS '회원 ID (로그인용)';
COMMENT ON COLUMN members.email        IS '이메일';
COMMENT ON COLUMN members.password     IS '비밀번호 (BCrypt 해시)';
COMMENT ON COLUMN members.mem_name     IS '회원 이름';
COMMENT ON COLUMN members.mobile       IS '휴대폰 번호';
COMMENT ON COLUMN members.role_id      IS '권한 FK (1인당 1개)';

-- 배송지
CREATE TABLE member_addresses (
    address_id      BIGSERIAL       PRIMARY KEY,
    member_id       BIGINT          NOT NULL,
    alias           VARCHAR(50),
    recipient       VARCHAR(50)     NOT NULL,
    mobile          VARCHAR(20)     NOT NULL,
    zipcode         VARCHAR(10)     NOT NULL,
    address         VARCHAR(200)    NOT NULL,
    address_detail  VARCHAR(200),
    is_default      BOOLEAN         NOT NULL DEFAULT false,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_address_member
        FOREIGN KEY (member_id) REFERENCES members (member_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_address_member ON member_addresses (member_id);

COMMENT ON TABLE  member_addresses                  IS '회원 배송지';
COMMENT ON COLUMN member_addresses.alias            IS '배송지명 (집, 회사 등)';
COMMENT ON COLUMN member_addresses.recipient        IS '수령인';
COMMENT ON COLUMN member_addresses.mobile           IS '수령인 연락처';
COMMENT ON COLUMN member_addresses.is_default       IS '기본 배송지 여부';

-- ============================================
-- 2. 도서 도메인
-- ============================================

-- 카테고리
CREATE TABLE categories (
    category_id         VARCHAR(50)       PRIMARY KEY,
    category_name       VARCHAR(50)     NOT NULL UNIQUE,
    created_at           TIMESTAMP       NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE categories IS '도서 카테고리';
COMMENT ON COLUMN categories.category_id IS '카테고리 번호';
COMMENT ON COLUMN categories.category_name IS '카테고리 이름';


-- 도서
CREATE TABLE books (
    book_id         BIGSERIAL       PRIMARY KEY,
    category_id     VARCHAR(50)      NOT NULL,
    title           VARCHAR(200)    NOT NULL,
    author          VARCHAR(100)    NOT NULL,
    publisher       VARCHAR(100),
    isbn            VARCHAR(13)     UNIQUE,
    price           INTEGER         NOT NULL,
    sale_price      INTEGER         NOT NULL,
    stock           INTEGER         NOT NULL DEFAULT 0,
    description     TEXT,
    image_url       VARCHAR(500),
    published_date  DATE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_book_category
        FOREIGN KEY (category_id) REFERENCES categories (category_id),
    CONSTRAINT chk_price_positive CHECK (price >= 0),
    CONSTRAINT chk_sale_price_positive  CHECK (sale_price >= 0),
    CONSTRAINT chk_stock_positive   CHECK (stock >= 0)
);

CREATE INDEX idx_book_category ON books (category_id);
CREATE INDEX idx_book_title    ON books (title);
CREATE INDEX idx_book_author   ON books (author);

COMMENT ON TABLE  books                IS '도서';
COMMENT ON COLUMN books.price          IS '정가';
COMMENT ON COLUMN books.sale_price     IS '판매가';
COMMENT ON COLUMN books.stock          IS '재고 수량';

-- 태그
CREATE TABLE tags (
    tag_id  VARCHAR(50)       PRIMARY KEY,
    name    VARCHAR(50)     NOT NULL UNIQUE
);
COMMENT ON TABLE tags IS '도서 태그 (베스트셀러, 신간 등)';


-- 도서-태그 매핑 (다대다)
CREATE TABLE book_tags (
    book_id BIGINT NOT NULL,
    tag_id  VARCHAR(50) NOT NULL,
    PRIMARY KEY (book_id, tag_id),

    CONSTRAINT fk_booktag_book
        FOREIGN KEY (book_id) REFERENCES books (book_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_booktag_tag
        FOREIGN KEY (tag_id) REFERENCES tags (tag_id)
        ON DELETE CASCADE
);

COMMENT ON TABLE book_tags IS '도서-태그 매핑';

-- ============================================
-- 3. 장바구니 도메인
-- ============================================

CREATE TABLE cart_items (
    cart_item_id    BIGSERIAL       PRIMARY KEY,
    member_id       BIGINT          NOT NULL,
    book_id         BIGINT          NOT NULL,
    quantity        INTEGER         NOT NULL DEFAULT 1,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_cart_member
        FOREIGN KEY (member_id) REFERENCES members (member_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_cart_book
        FOREIGN KEY (book_id) REFERENCES books (book_id)
        ON DELETE CASCADE,
    CONSTRAINT uq_cart_member_book
        UNIQUE (member_id, book_id),
    CONSTRAINT chk_cart_quantity
        CHECK (quantity > 0)
);

CREATE INDEX idx_cart_member ON cart_items (member_id);

COMMENT ON TABLE  cart_items            IS '장바구니';
COMMENT ON COLUMN cart_items.quantity    IS '수량';

-- ============================================
-- 4. 주문/결제 도메인
-- ============================================

-- 주문
CREATE TABLE orders (
    order_id        BIGSERIAL       PRIMARY KEY,
    order_number    VARCHAR(30)     NOT NULL UNIQUE,
    member_id       BIGINT          NOT NULL,
    recipient       VARCHAR(50)     NOT NULL,
    mobile          VARCHAR(20)     NOT NULL,
    zipcode         VARCHAR(10)     NOT NULL,
    address         VARCHAR(200)    NOT NULL,
    address_detail  VARCHAR(200),
    total_price     INTEGER         NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'ORDERED',
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_order_member
        FOREIGN KEY (member_id) REFERENCES members (member_id),
    CONSTRAINT chk_order_status
        CHECK (status IN ('ORDERED', 'PAID', 'SHIPPING', 'DELIVERED', 'CANCELLED')),
    CONSTRAINT chk_total_price_positive
        CHECK (total_price >= 0)
);

CREATE INDEX idx_order_member ON orders (member_id);
CREATE INDEX idx_order_status ON orders (status);
CREATE INDEX idx_order_created ON orders (created_at DESC);

COMMENT ON TABLE  orders                    IS '주문';
COMMENT ON COLUMN orders.order_number       IS '주문번호 (ORD-20260211-0001 형식)';
COMMENT ON COLUMN orders.recipient          IS '수령인 (주문 시점 스냅샷)';
COMMENT ON COLUMN orders.mobile             IS '수령인 연락처 (주문 시점 스냅샷)';
COMMENT ON COLUMN orders.status             IS '주문 상태: ORDERED → PAID → SHIPPING → DELIVERED / CANCELLED';

-- 주문 상세
CREATE TABLE order_items (
    order_item_id   BIGSERIAL       PRIMARY KEY,
    order_id        BIGINT          NOT NULL,
    book_id         BIGINT          NOT NULL,
    quantity        INTEGER         NOT NULL,
    price           INTEGER         NOT NULL,

    CONSTRAINT fk_orderitem_order
        FOREIGN KEY (order_id) REFERENCES orders (order_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_orderitem_book
        FOREIGN KEY (book_id) REFERENCES books (book_id),
    CONSTRAINT chk_oi_quantity
        CHECK (quantity > 0),
    CONSTRAINT chk_oi_price
        CHECK (price >= 0)
);

CREATE INDEX idx_orderitem_order ON order_items (order_id);

COMMENT ON TABLE  order_items           IS '주문 상세';
COMMENT ON COLUMN order_items.price     IS '주문 당시 단가 (스냅샷)';

-- 결제
CREATE TABLE payments (
    payment_id  BIGSERIAL       PRIMARY KEY,
    order_id    BIGINT          NOT NULL UNIQUE,
    method      VARCHAR(30)     NOT NULL,
    amount      INTEGER         NOT NULL,
    status      VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    paid_at     TIMESTAMP,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id) REFERENCES orders (order_id),
    CONSTRAINT chk_payment_status
        CHECK (status IN ('PENDING', 'COMPLETED', 'REFUNDED', 'FAILED')),
    CONSTRAINT chk_payment_amount
        CHECK (amount >= 0)
);

COMMENT ON TABLE  payments              IS '결제';
COMMENT ON COLUMN payments.method       IS '결제수단 (CARD, BANK_TRANSFER 등)';
COMMENT ON COLUMN payments.status       IS '결제 상태: PENDING → COMPLETED / REFUNDED / FAILED';
COMMENT ON COLUMN payments.paid_at      IS '결제 완료 일시';

-- ============================================
-- 5. 리뷰 도메인
-- ============================================

CREATE TABLE reviews (
    review_id       BIGSERIAL       PRIMARY KEY,
    member_id       BIGINT          NOT NULL,
    book_id         BIGINT          NOT NULL,
    order_item_id   BIGINT          NOT NULL UNIQUE,
    rating          SMALLINT        NOT NULL,
    content         TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_review_member
        FOREIGN KEY (member_id) REFERENCES members (member_id),
    CONSTRAINT fk_review_book
        FOREIGN KEY (book_id) REFERENCES books (book_id),
    CONSTRAINT fk_review_orderitem
        FOREIGN KEY (order_item_id) REFERENCES order_items (order_item_id),
    CONSTRAINT chk_rating_range
        CHECK (rating >= 1 AND rating <= 5)
);

CREATE INDEX idx_review_book   ON reviews (book_id);
CREATE INDEX idx_review_member ON reviews (member_id);

COMMENT ON TABLE  reviews                   IS '도서 리뷰';
COMMENT ON COLUMN reviews.order_item_id     IS '주문 상세 ID (구매자만 리뷰 가능, 중복 방지)';
COMMENT ON COLUMN reviews.rating            IS '평점 (1~5)';

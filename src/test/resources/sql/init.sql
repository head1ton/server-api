INSERT INTO `members`
(member_id, birth, email, name, nickname, password, status, `role`, sns_id, sns_type, created_at,
 modified_at)
VALUES (1, '19941030', 'member@gmail.com', '김멤버', 'member',
        '$2a$10$ENshju5t9B/IvGCdsP838.JxncPY70wHOfBQkrkoN.QxonMHhaxyu', 'NORMAL', 'MEMBER', null,
        null, '2023-09-26 13:51:08.615676000', '2023-09-26 13:51:08.615676000');

-- seller1
INSERT INTO `members`
(member_id, birth, email, name, nickname, password, status, `role`, sns_id, sns_type, created_at,
 modified_at)
VALUES (2, '19941030', 'seller@gmail.com', '김셀러', 'seller',
        '$2a$10$b42VDBUg7aKblEFbS6KFTugXypdFLRD3xKd2EbKKP6ONrznOcVz2O', 'NORMAL', 'SELLER', null,
        null, '2023-09-26 13:51:08.615676000', '2023-09-26 13:51:08.615676000');

INSERT INTO seller
(seller_id, member_id, company, email, zonecode, address, address_detail, tel, created_at,
 modified_at)
VALUES (1, 2, '회사명', 'mail@gmail.com', '1234', '주소', '상세주소', '01012341234',
        '2023-09-26 15:09:27.143630000', '2023-09-26 15:09:27.143630000');


-- seller2
INSERT INTO `members`
(member_id, birth, email, name, nickname, password, status, `role`, sns_id, sns_type, created_at,
 modified_at)
VALUES (3, '19941030', 'seller2@gmail.com', '김투셀러', 'seller2',
        '$2a$10$45eEiSOQxLtJx0rhmAZ1aeA8/B6XxsDk3MM0jf26E/3Drf.a.05im', 'NORMAL', 'SELLER', null,
        null, '2023-09-26 13:51:08.615676000', '2023-09-26 13:51:08.615676000');

INSERT INTO seller
(seller_id, member_id, company, email, zonecode, address, address_detail, tel, created_at,
 modified_at)
VALUES (2, 3, '회사명2', 'mail2@gmail.com', '1234', '주소', '상세주소', '01012341234',
        '2023-09-26 15:09:27.143630000', '2023-09-26 15:09:27.143630000');
-- category
INSERT INTO category
    (category_id, name, status, created_at, modified_at)
VALUES (1, '뷰티', 'USE', '2023-09-26 15:09:27.263900000', '2023-09-26 15:09:27.263900000');
INSERT INTO category
    (category_id, name, status, created_at, modified_at)
VALUES (2, '건강식품', 'USE', '2023-09-26 15:09:27.263900000', '2023-09-26 15:09:27.263900000');
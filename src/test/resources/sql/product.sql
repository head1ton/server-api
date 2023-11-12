INSERT INTO product
(product_id, category_id, main_explanation, main_title, main_image, origin, origin_price, price,
 producer, product_main_explanation, product_sub_explanation, purchase_inquiry, image1, image2,
 image3, view_cnt, ea, status, seller_id, created_at, modified_at, `type`)
VALUES (1, 1, '메인 설명', '마스크', 'https://image1', '원산지', 12000, 10000, '공급자', '상품 메인 설명', '상품 보조 설명',
        '취급방법', 'https://image1', 'https://image1', 'https://image1', 0, 100, 'NORMAL', 1,
        '2023-09-26 15:09:27.363409000', '2023-09-26 15:09:27.363409000', 'OPTION');

INSERT INTO product
(product_id, category_id, main_explanation, main_title, main_image, origin, origin_price, price,
 producer, product_main_explanation, product_sub_explanation, purchase_inquiry, image1, image2,
 image3, view_cnt, ea, status, seller_id, created_at, modified_at, `type`)
VALUES (2, 1, '메인 설명', '배', 'https://image1', '원산지', 12000, 10000, '공급자', '상품 메인 설명', '상품 보조 설명',
        '취급방법', 'https://image1', 'https://image1', 'https://image1', 0, 100, 'NORMAL', 1,
        '2023-09-26 15:09:27.363409000', '2023-09-26 15:09:27.363409000', 'OPTION');

INSERT INTO product
(product_id, category_id, main_explanation, main_title, main_image, origin, origin_price, price,
 producer, product_main_explanation, product_sub_explanation, purchase_inquiry, image1, image2,
 image3, view_cnt, ea, status, seller_id, created_at, modified_at, `type`)
VALUES (3, 1, '메인 설명', '일반상품(옵션없음)', 'https://image1', '원산지', 12000, 10000, '공급자', '상품 메인 설명',
        '상품 보조 설명', '취급방법', 'https://image1', 'https://image1', 'https://image1', 0, 100, 'NORMAL',
        1, '2023-09-26 15:09:27.363409000', '2023-09-26 15:09:27.363409000', 'NORMAL');

INSERT INTO product
(product_id, category_id, main_explanation, main_title, main_image, origin, origin_price, price,
 producer, product_main_explanation, product_sub_explanation, purchase_inquiry, image1, image2,
 image3, view_cnt, ea, status, seller_id, created_at, modified_at, `type`)
VALUES (4, 1, '메인 설명', '일반상품2(옵션없음)', 'https://image1', '원산지', 12000, 10000, '공급자', '상품 메인 설명',
        '상품 보조 설명', '취급방법', 'https://image1', 'https://image1', 'https://image1', 0, 100, 'NORMAL',
        2, '2023-09-26 15:09:27.363409000', '2023-09-26 15:09:27.363409000', 'NORMAL');


INSERT INTO `options`
(option_id, created_at, ea, extra_price, modified_at, name, status, product_id)
VALUES (1, '2023-09-26 15:09:27.363409000', 1000, 1000, '2023-09-26 15:09:27.363409000', '상품1옵션1',
        'NORMAL', 1);

INSERT INTO `options`
(option_id, created_at, ea, extra_price, modified_at, name, status, product_id)
VALUES (2, '2023-09-26 15:09:27.363409000', 1000, 1000, '2023-09-26 15:09:27.363409000', '상품2옵션1',
        'NORMAL', 2);

INSERT INTO `options`
(option_id, created_at, ea, extra_price, modified_at, name, status, product_id)
VALUES (3, '2023-09-26 15:09:27.363409000', 1000, 1000, '2023-09-26 15:09:27.363409000', '상품1옵션2',
        'NORMAL', 1);
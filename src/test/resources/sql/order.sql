INSERT INTO orders
(order_id, member_id, order_number, order_name, status, created_at, modified_at)
VALUES (1, 1, 'ORDER-20231010-1', '주문 이름', 'COMPLETE', '2023-09-26 15:09:27.363409000',
        '2023-09-26 15:09:27.363409000');

INSERT INTO order_item
(order_item_id, created_at, ea, modified_at, product_price, product_total_price, status, order_id,
 product_id, option_id)
VALUES (1, '2023-09-26 15:09:27.363409000', 2, '2023-09-26 15:09:27.363409000', 1000, 2000, 'ORDER',
        1, 1, 3);

INSERT INTO order_item
(order_item_id, created_at, ea, modified_at, product_price, product_total_price, status, order_id,
 product_id, option_id)
VALUES (2, '2023-09-26 15:09:27.363409000', 1, '2023-09-26 15:09:27.363409000', 3000, 3000, 'ORDER',
        1, 3, null);

INSERT INTO delivery
(delivery_id, created_at, modified_at, owner_address, owner_address_detail, owner_name, owner_tel,
 owner_zonecode, recipient_address, recipient_address_detail, recipient_name, recipient_tel,
 recipient_zonecode, status, order_id, order_item_id)
VALUES (1, '2023-09-26 15:09:27.363409000', '2023-09-26 15:09:27.363409000', '주문자 주소', '주문자 상세 주소',
        '주문자명', '01012341234', '123-12', '수령인 주소', '수령인 상세 주소', '수령인명', '01043124321', '321-21',
        'COMPLETE', 1, 1);


-- 2번 주문
INSERT INTO orders
(order_id, member_id, order_number, order_name, status, created_at, modified_at)
VALUES (2, 2, 'ORDER-20231010-2', '주문 이름2', 'COMPLETE', '2023-09-26 15:09:27.363409000',
        '2023-09-26 15:09:27.363409000');

INSERT INTO order_item
(order_item_id, created_at, ea, modified_at, product_price, product_total_price, status, order_id,
 product_id, option_id)
VALUES (3, '2023-09-26 15:09:27.363409000', 5, '2023-09-26 15:09:27.363409000', 3000, 15000,
        'ORDER', 2, 3, null);

INSERT INTO delivery
(delivery_id, created_at, modified_at, owner_address, owner_address_detail, owner_name, owner_tel,
 owner_zonecode, recipient_address, recipient_address_detail, recipient_name, recipient_tel,
 recipient_zonecode, status, order_id, order_item_id)
VALUES (2, '2023-09-26 15:09:27.363409000', '2023-09-26 15:09:27.363409000', '주문자 주소', '주문자 상세 주소',
        '주문자명', '01012341234', '123-12', '수령인 주소', '수령인 상세 주소', '수령인명', '01043124321', '321-21',
        'COMPLETE', 2, 3);

-- 2번 seller
-- 3번 주문
INSERT INTO orders
(order_id, member_id, order_number, order_name, status, created_at, modified_at)
VALUES (3, 2, 'ORDER-20231010-3', '주문 이름3', 'COMPLETE', '2023-09-26 15:09:27.363409000',
        '2023-09-26 15:09:27.363409000');

INSERT INTO order_item
(order_item_id, created_at, ea, modified_at, product_price, product_total_price, status, order_id,
 product_id, option_id)
VALUES (4, '2023-09-26 15:09:27.363409000', 5, '2023-09-26 15:09:27.363409000', 1000, 5000, 'ORDER',
        3, 4, null);

INSERT INTO delivery
(delivery_id, created_at, modified_at, owner_address, owner_address_detail, owner_name, owner_tel,
 owner_zonecode, recipient_address, recipient_address_detail, recipient_name, recipient_tel,
 recipient_zonecode, status, order_id, order_item_id)
VALUES (3, '2023-09-26 15:09:27.363409000', '2023-09-26 15:09:27.363409000', '주문자 주소', '주문자 상세 주소',
        '주문자명', '01012341234', '123-12', '수령인 주소', '수령인 상세 주소', '수령인명', '01043124321', '321-21',
        'COMPLETE', 3, 4);
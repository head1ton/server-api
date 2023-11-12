INSERT INTO orders
(order_id, member_id, order_number, order_name, status, created_at, modified_at)
VALUES (1, 1, 'ORDER-20231010-1', '주문 이름', 'COMPLETE', '2023-09-26 15:09:27.363409000',
        '2023-09-26 15:09:27.363409000');

INSERT INTO order_item
(order_item_id, created_at, ea, modified_at, product_price, product_total_price, status, order_id,
 product_id, option_id)
VALUES (1, '2023-09-26 15:09:27.363409000', 2, '2023-09-26 15:09:27.363409000', 1000, 2000, 'ORDER',
        1, 1, 3);

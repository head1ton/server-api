insert into Member (email, name, nickname, password, birth, role, created_at, modified_at)
values ('user@gmail.com', '김멤버', '멤버',
        '$2a$10$0WXVKP1oBfC0oOYRDt8uYuWJcmgK9QY1bAqkg6HwytyhaCoxBdZ1i', '20001122', 'MEMBER',
        '2023-05-31 10:21:03.961557', '2023-05-31 10:21:03.961557');
insert into Member (email, name, nickname, password, birth, role, created_at, modified_at)
values ('seller@gmail.com', '김셀러', '셀러',
        '$2a$10$0WXVKP1oBfC0oOYRDt8uYuWJcmgK9QY1bAqkg6HwytyhaCoxBdZ1i', '20001122', 'SELLER',
        '2023-05-31 10:21:03.961557', '2023-05-31 10:21:03.961557');
insert into Member (email, name, nickname, password, birth, role, created_at, modified_at)
values ('seller2@gmail.com', '김셀러2', '셀러2',
        '$2a$10$0WXVKP1oBfC0oOYRDt8uYuWJcmgK9QY1bAqkg6HwytyhaCoxBdZ1i', '20001122', 'SELLER',
        '2023-05-31 10:21:03.961557', '2023-05-31 10:21:03.961557');

insert into Category (name, status, created_at, modified_at)
values ('화장품', 'USE', '2023-07-04 10:21:03.961557', '2023-07-04 10:21:03.961557');
insert into Category (name, status, created_at, modified_at)
values ('건강식품', 'USE', '2023-07-04 10:21:03.961557', '2023-07-04 10:21:03.961557');
insert into Category (name, status, created_at, modified_at)
values ('생활용품', 'USE', '2023-07-04 10:21:03.961557', '2023-07-04 10:21:03.961557');
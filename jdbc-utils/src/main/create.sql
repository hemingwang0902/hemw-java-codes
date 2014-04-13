create table USER
(
  id                  NUMBER(20) not null,
  username      VARCHAR2(100) not null,
  birthday        TIMESTAMP default sysdate not null
);
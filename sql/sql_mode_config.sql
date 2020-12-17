-- modify sql_mode
-- 去掉 NO_ZERO_DATE NO_ZERO_IN_DATE ONLY_FULL_GROUP_BY
show variables like 'sql_mode';
set session
sql_mode = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
show variables like 'sql_mode';

-- modify charset
show create table `t_role`;
alter table `t_role` default character set utf8;
alter table `t_role` change intro intro varchar(255) character set utf8;

-- auto increment start number
alter table `t_role` AUTO_INCREMENT=10241024;

-- slow query log
set global slow_query_log='ON';

set global long_query_time=2;

show variables like '%slow_query_log_file%';


-- procedure
delimiter $$
create procedure pre()
begin
    declare i int;
    set i=0;
    while i<1000000 do
            insert into t_active(user_id, identity, status) values(10000+i, 0, 1) ;
            set i=i+1;
        end while;
end
$$
call pre();
DROP procedure pre;


-- 重复值较高的字段索引性能测试
SELECT SQL_NO_CACHE COUNT(distinct (user_id)) FROM t_active  GROUP BY identity;
EXPLAIN SELECT COUNT(distinct (user_id)) FROM t_active GROUP BY identity;

SELECT SQL_NO_CACHE count(distinct (user_id)) FROM t_active WHERE identity = 0;
EXPLAIN SELECT count(distinct (user_id)) FROM t_active WHERE identity = 0;

-- distinct 和 group by 在有没有索引下的性能测试
SELECT SQL_NO_CACHE distinct (identity) FROM  t_active;
SELECT SQL_NO_CACHE identity FROM t_active GROUP BY identity;

SELECT SQL_NO_CACHE distinct (user_id) FROM t_active;
SELECT SQL_NO_CACHE user_id FROM t_active GROUP BY user_id;

-- 事务RR级别下，快照读和当前读的问题
-- T1：如果使用 select .. for update 则使用当前读，并和update、delete一样会锁定相关行，
-- 慎用 for update, 尤其 where 的条件不是主键和唯一索引的时候，使用不当会导致死锁
begin;
select money from rr_test for update ;
-- = 的方式未查询到数据不会锁表
select money from rr_test where name = 'unknown' for update ;
-- LIKE 的方式不管查到还是没查到相关数据都会锁表
select money from rr_test where name LIKE '%unknown%' for update ;
select money from rr_test where name LIKE '%a%' for update ;

update rr_test set money = 200 where name = 'iomd';
select * from rr_test;
commit ;

-- T2：
begin;
update rr_test set money = 1000 where name = 'iomd';
commit ;

-- T3
select * from rr_test;



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



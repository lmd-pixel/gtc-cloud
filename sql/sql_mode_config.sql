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


-- 考虑一个复杂查询：获取主播列表，推荐表中数据（level排序）+粉丝数排序
-- 方案1： union 联合，但每个子select语句无法 order by，如果需要order by 必须结合limit，
--        问题是limit的值不太好具体指定，可以指定个很大的值，也不太好结合jpa去使用

-- 方案2： union 联合，每个子select语句增加需要排序的字段，最后统一order by，
--        问题是增加了自定义排序字段后，两个子查询的结果集无法再通过union 去重，因为排序字段的值不同

-- 方案3： union all 联合查询，不实用union 去重，对比方案2，自己去重，
--        第二个select子句使用left join(a join b 不包含交集部分：通过最后判断 b.key is null)

-- 结合jpa的分页，countQuery可以直接使用条件筛选部分，因为最终都是t_user的筛选用户
(SELECT tu.*, 1 AS sort1, tu.follows AS sort2 FROM `gtc-social-db`.t_user tu
INNER JOIN t_recommend tr ON tr.type = 0 AND tr.recommend = 1 AND tr.user_id = tu.id
WHERE identity = 1 AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL)
ORDER BY tr.level) UNION ALL
(SELECT tu2.*, 0 AS sort1, tu2.follows AS sort2 FROM `gtc-social-db`.t_user tu2
LEFT JOIN t_recommend tr2 ON tr2.type = 0 AND tr2.recommend = 1 AND tr2.user_id = tu2.id
WHERE identity = 1 AND (gender LIKE CONCAT('%', ?1, '%') OR ?1 IS NULL) AND tr2.id IS NULL)
ORDER BY sort1 DESC , sort2 DESC;

# 统计查询
SELECT days,
       SUM(newUser) as newUser,
       SUM(newAnchor) AS newAnchor,
       SUM(totalRecharge) AS totalRecharge,
       SUM(newRecharge) AS newRecharge,
       SUM(totalConsume) AS totalConsume,
       SUM(newConsume) AS newConsume
FROM(

        SELECT DATE_FORMAT(tu.create_time, '%Y-%m-%d') days, 0 AS newUser, 0 AS newAnchor, 0 AS totalRecharge, 0 AS newRecharge, 0 AS totalConsume, 0 AS newConsume
        FROM `gtc-social-db`.t_user tu WHERE tu.create_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00' GROUP BY days

        UNION ALL
        SELECT DATE_FORMAT(tu2.create_time, '%Y-%m-%d') days, COUNT(tu2.id) AS newUser, 0 AS newAnchor, 0 AS totalRecharge, 0 AS newRecharge, 0 AS totalConsume, 0 AS newConsume
        FROM `gtc-social-db`.t_user tu2 WHERE tu2.create_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00' GROUP BY days

        UNION ALL
        SELECT DATE_FORMAT(tu3.create_time, '%Y-%m-%d') days, 0 AS newUser, COUNT(tu3.id) AS newAnchor, 0 AS totalRecharge, 0 AS newRecharge, 0 AS totalConsume, 0 AS newConsume
        FROM `gtc-social-db`.t_user tu3 WHERE tu3.identity = 1 AND tu3.create_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00' GROUP BY days

        UNION ALL
        SELECT DATE_FORMAT(tr.creat_time, '%Y-%m-%d') days, 0 AS newUser, 0 AS newAnchor, COUNT(DISTINCT tr.user_id) AS totalRecharge, 0 AS newRecharge, 0 AS totalConsume, 0 AS newConsume
        FROM `gtc-social-db`.t_recharge tr WHERE tr.status = 20 AND tr.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00' GROUP BY days

        UNION ALL
        SELECT DATE_FORMAT(tr2.creat_time, '%Y-%m-%d') days, 0 AS newUser, 0 AS newAnchor, 0 AS totalRecharge, COUNT(DISTINCT tr2.user_id) AS newRecharge, 0 AS totalConsume, 0 AS newConsume
        FROM `gtc-social-db`.t_recharge tr2 WHERE tr2.status = 20 AND tr2.is_first = 1 AND tr2.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00' GROUP BY days

        UNION ALL
        SELECT DATE_FORMAT(bill.creat_time, '%Y-%m-%d') days, 0 AS newUser, 0 AS newAnchor, 0 AS totalRecharge, 0 AS newRecharge, COUNT(DISTINCT bill.user_id_from) AS totalConsume, 0 AS newConsume
        FROM (
                 SELECT tcb.creat_time, tcb.user_id_from FROM t_call_bill tcb WHERE tcb.valid = 1 AND tcb.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
                 UNION ALL
                 SELECT tmb.creat_time, tmb.user_id_from FROM t_message_bill tmb WHERE tmb.valid = 1 AND tmb.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
                 UNION ALL
                 SELECT tgb.creat_time, tgb.user_id_from FROM t_gift_bill tgb WHERE tgb.valid = 1 AND tgb.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
             ) bill
        GROUP BY days

        UNION ALL
        SELECT DATE_FORMAT(bill2.creat_time, '%Y-%m-%d') days, 0 AS newUser, 0 AS newAnchor, 0 AS totalRecharge, 0 AS newRecharge, 0 AS totalConsume, COUNT(DISTINCT bill2.user_id_from) AS newConsume
        FROM (
                 SELECT tcb.creat_time, tcb.user_id_from FROM t_call_bill tcb WHERE tcb.valid = 1 AND tcb.is_first = 1 AND tcb.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
                 UNION ALL
                 SELECT tmb.creat_time, tmb.user_id_from FROM t_message_bill tmb WHERE tmb.valid = 1 AND tmb.is_first = 1 AND tmb.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
                 UNION ALL
                 SELECT tgb.creat_time, tgb.user_id_from FROM t_gift_bill tgb WHERE tgb.valid = 1 AND tgb.is_first = 1 AND tgb.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
             ) bill2
        GROUP BY days

    ) t GROUP BY days;


#统计查询
SELECT days,
       SUM(newUser) as newUser,
       SUM(newAnchor) AS newAnchor,
       SUM(totalRecharge) AS totalRecharge,
       SUM(newRecharge) AS newRecharge,
       SUM(totalConsume) AS totalConsume,
       SUM(newConsume) AS newConsume
FROM(

        SELECT DATE_FORMAT(tu.create_time, '%Y-%m-%d') days,
               0 AS newUser, 0 AS newAnchor,
               0 AS totalRecharge, 0 AS newRecharge,
               0 AS totalConsume, 0 AS newConsume
        FROM `gtc-social-db`.t_user tu WHERE tu.create_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
        GROUP BY days

        UNION ALL
        SELECT DATE_FORMAT(tu2.create_time, '%Y-%m-%d') days,
               COUNT(tu2.id) AS newUser, 0 AS newAnchor,
               0 AS totalRecharge, 0 AS newRecharge,
               0 AS totalConsume, 0 AS newConsume
        FROM `gtc-social-db`.t_user tu2 WHERE tu2.create_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
        GROUP BY days

        UNION ALL
        SELECT DATE_FORMAT(tu3.create_time, '%Y-%m-%d') days,
               0 AS newUser, COUNT(tu3.id) AS newAnchor,
               0 AS totalRecharge, 0 AS newRecharge,
               0 AS totalConsume, 0 AS newConsume
        FROM `gtc-social-db`.t_user tu3 WHERE tu3.identity = 1 AND tu3.create_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
        GROUP BY days

        UNION ALL
        SELECT DATE_FORMAT(tr.creat_time, '%Y-%m-%d') days,
               0 AS newUser, 0 AS newAnchor,
               COUNT(DISTINCT tr.user_id) AS totalRecharge, 0 AS newRecharge,
               0 AS totalConsume, 0 AS newConsume
        FROM `gtc-social-db`.t_recharge tr WHERE tr.status = 20 AND tr.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
        GROUP BY days

        UNION ALL
        SELECT DATE_FORMAT(tr.creat_time, '%Y-%m-%d') days,
               0 AS newUser, 0 AS newAnchor,
               0 AS totalRecharge, COUNT(DISTINCT tr.user_id) AS newRecharge,
               0 AS totalConsume, 0 AS newConsume
        FROM `gtc-social-db`.t_recharge tr
                 LEFT JOIN t_recharge tr2 ON tr2.status = 20 AND tr2.id = tr.id AND tr2.creat_time < '2020-11-01 00:00:00'
        WHERE tr.status = 20 AND tr.is_first = 1 AND tr.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00' AND tr2.id IS NULL
        GROUP BY days

        UNION ALL
        SELECT DATE_FORMAT(bill.creat_time, '%Y-%m-%d') days,
               0 AS newUser, 0 AS newAnchor,
               0 AS totalRecharge, 0 AS newRecharge,
               COUNT(DISTINCT bill.user_id_from) AS totalConsume, 0 AS newConsume
        FROM (
                 SELECT tcb.creat_time, tcb.user_id_from FROM t_call_bill tcb
                 WHERE tcb.valid = 1 AND tcb.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
                 UNION ALL
                 SELECT tmb.creat_time, tmb.user_id_from FROM t_message_bill tmb
                 WHERE tmb.valid = 1 AND tmb.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
                 UNION ALL
                 SELECT tgb.creat_time, tgb.user_id_from FROM t_gift_bill tgb
                 WHERE tgb.valid = 1 AND tgb.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00'
             ) bill
        GROUP BY days

        UNION ALL
        SELECT DATE_FORMAT(bill2.creat_time, '%Y-%m-%d') days,
               0 AS newUser, 0 AS newAnchor,
               0 AS totalRecharge, 0 AS newRecharge,
               0 AS totalConsume, COUNT(DISTINCT bill2.user_id_from) AS newConsume
        FROM (
                 SELECT tcb.creat_time, tcb.user_id_from FROM t_call_bill tcb
                                                                  LEFT JOIN t_call_bill tcb2 ON tcb2.valid = 1 AND tcb2.id = tcb.id AND tcb2.creat_time < '2020-11-01 00:00:00'
                 WHERE tcb.valid = 1 AND tcb.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00' AND tcb2.id IS NULL
                 UNION ALL
                 SELECT tmb.creat_time, tmb.user_id_from FROM t_message_bill tmb
                                                                  LEFT JOIN t_message_bill tmb2 ON tmb2.valid = 1 AND tmb2.id = tmb.id AND tmb2.creat_time < '2020-11-01 00:00:00'
                 WHERE tmb.valid = 1 AND tmb.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00' AND tmb2.id IS NULL
                 UNION ALL
                 SELECT tgb.creat_time, tgb.user_id_from FROM t_gift_bill tgb
                                                                  LEFT JOIN t_gift_bill tgb2 ON tgb2.valid = 1 AND tgb2.id = tgb.id AND tgb2.creat_time < '2020-11-01 00:00:00'
                 WHERE tgb.valid = 1 AND tgb.creat_time BETWEEN '2020-11-01 00:00:00' AND '2020-12-31 00:00:00' AND tgb2.id IS NULL
             ) bill2
        GROUP BY days

    ) t GROUP BY days;



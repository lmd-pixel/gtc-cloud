-- modify sql_mode
show variables like 'sql_mode';
set session
sql_mode = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
show variables like 'sql_mode';

-- modify charset
show create table `t_role`;
alter table `t_role` default character set utf8;
alter table `t_role` change intro intro varchar(255) character set utf8;



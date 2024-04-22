
--所有储值卡余额
select sum(balance) as sum_balance from tz_user_balance
--储值用户数量
select count(*) as balance_user_cnt, sum(balance) as sum_balance  from tz_user_balance where balance > 0


--当日消费余额金额 和 订单笔数
select count(*) as use_balance_order_cnt, sum(use_balance) use_balance_sum from tz_user_balance_detail where 1=1 and user_id!='o7hh869Yv8oVzVJ8UEUPorTqeMI8' and use_time >='2024-04-22' and use_balance<0 order by  use_time desc


--当月消费余额金额 和 订单笔数
select count(*) as use_balance_order_cnt, sum(use_balance) use_balance_sum from tz_user_balance_detail where 1=1 and user_id!='o7hh869Yv8oVzVJ8UEUPorTqeMI8' and use_time >='2024-04-01' and use_balance<0 order by  use_time desc




--当日充值余额金额 和 订单笔数 包含订单关闭退款
select count(*) as use_balance_order_cnt, sum(use_balance) use_balance_sum from tz_user_balance_detail where 1=1 and user_id!='o7hh869Yv8oVzVJ8UEUPorTqeMI8' and use_time >='2024-04-22' and use_balance>0 order by  use_time desc


--当月充值余额金额 和 订单笔数 包含订单关闭退款
select count(*) as use_balance_order_cnt, sum(use_balance) use_balance_sum from tz_user_balance_detail where 1=1 and user_id!='o7hh869Yv8oVzVJ8UEUPorTqeMI8' and use_time >='2024-03-01' and use_balance>0 order by  use_time desc


select * from tz_order where  create_time  >='2024-04-22' order by create_time desc

--当日总订单数量和订单金额  包含未支付和已取消的订单
select count(*) as  total_order, sum(actual_total) as total_order_actual,sum(product_nums) as total_product_nums from tz_order where  create_time  >='2024-04-22' order by create_time desc

--当日订单支付数量和订单金额  包含已退款的
select count(*) as  total_order, sum(actual_total) as total_order_actual,sum(product_nums) as total_product_nums from tz_order where  create_time  >='2024-04-22' and is_payed=1 order by create_time desc

--当日订单支付方式统计
select count(*) as  total_order, pay_type from tz_order where  create_time  >='2024-04-22' group by pay_type

--当日订单支付成功统计
select count(*) as  total_order, is_payed from tz_order where  create_time  >='2024-04-22' group by is_payed

--当日订单状态统计
select count(*) as  total_order, status from tz_order where  create_time  >='2024-04-22' group by status

--当日订单支付成功统计
select count(*) as  total_order, is_payed from tz_order where  create_time  >='2024-04-22' group by is_payed
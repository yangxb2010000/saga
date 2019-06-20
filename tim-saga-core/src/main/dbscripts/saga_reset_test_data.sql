use saga;
delete from t_saga_transaction;
delete from t_saga_participant;

use saga_account;
delete from account_history;
update account set balance = 300000;

use saga_inventory;
delete from inventory_history;
update inventory set inventory_count = 50000;

use saga_order;
delete from `order`;
GRANT SELECT, INSERT, UPDATE, DELETE ON accounts TO shop_accounts;
GRANT SELECT, INSERT, UPDATE, DELETE ON contacts TO shop_accounts;
GRANT SELECT, INSERT, UPDATE, DELETE ON addresses TO shop_accounts;

GRANT USAGE, SELECT ON SEQUENCE accounts_seq TO shop_accounts;
GRANT USAGE, SELECT ON SEQUENCE contacts_seq TO shop_accounts;
GRANT USAGE, SELECT ON SEQUENCE addresses_seq TO shop_accounts;

GRANT SELECT, INSERT, UPDATE ON products TO shop_orders;
GRANT SELECT ON accounts TO shop_orders;

GRANT USAGE, SELECT ON SEQUENCE products_seq TO shop_orders;
GRANT USAGE, SELECT ON SEQUENCE rates_seq TO shop_orders;
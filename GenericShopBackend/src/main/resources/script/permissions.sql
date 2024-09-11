GRANT SELECT, INSERT, UPDATE, DELETE ON accounts TO shop_accounts;
GRANT SELECT, INSERT, UPDATE, DELETE ON contacts TO shop_accounts;
GRANT SELECT, INSERT, UPDATE, DELETE ON addresses TO shop_accounts;

GRANT SELECT ON accounts, contacts, addresses TO shop_orders;

GRANT USAGE, SELECT ON SEQUENCE accounts_seq TO shop_accounts;
GRANT USAGE, SELECT ON SEQUENCE contacts_seq TO shop_accounts;
GRANT USAGE, SELECT ON SEQUENCE addresses_seq TO shop_accounts;

GRANT SELECT, INSERT, UPDATE, REFERENCES ON products TO shop_orders;
GRANT SELECT, INSERT, UPDATE ON categories TO shop_orders;
GRANT SELECT, INSERT ON orders, orders_products TO shop_orders;
GRANT SELECT ON accounts TO shop_orders;
GRANT CREATE ON DATABASE shop TO shop_orders;
GRANT CREATE ON SCHEMA public TO shop_orders;

GRANT USAGE, SELECT ON SEQUENCE products_seq TO shop_orders;
GRANT USAGE, SELECT ON SEQUENCE orders_seq TO shop_orders;
GRANT USAGE, SELECT ON SEQUENCE rates_seq TO shop_orders;
GRANT USAGE, SELECT ON SEQUENCE categories_seq TO shop_orders;

GRANT SELECT, INSERT, UPDATE ON yerbas, tvs, books, games TO shop_orders;
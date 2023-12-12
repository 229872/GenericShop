GRANT SELECT, INSERT, UPDATE, DELETE ON accounts TO shop_accounts;
GRANT SELECT, INSERT, UPDATE, DELETE ON people TO shop_accounts;
GRANT SELECT, INSERT, UPDATE, DELETE ON addresses TO shop_accounts;

GRANT USAGE, SELECT ON SEQUENCE accounts_seq TO shop_accounts;
GRANT USAGE, SELECT ON SEQUENCE people_seq TO shop_accounts;
GRANT USAGE, SELECT ON SEQUENCE addresses_seq TO shop_accounts;
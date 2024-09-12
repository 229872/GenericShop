GRANT SELECT, INSERT, UPDATE, DELETE ON accounts TO shop_accounts;
GRANT SELECT, INSERT, UPDATE, DELETE ON contacts TO shop_accounts;
GRANT SELECT, INSERT, UPDATE, DELETE ON addresses TO shop_accounts;

GRANT USAGE, SELECT ON SEQUENCE accounts_seq TO shop_accounts;
GRANT USAGE, SELECT ON SEQUENCE contacts_seq TO shop_accounts;
GRANT USAGE, SELECT ON SEQUENCE addresses_seq TO shop_accounts;
INSERT INTO addresses (id, version, is_archival, created_by, created_at, postal_code, country, city, street, house_number)
    VALUES (nextval('addresses_seq'), 0, false, 'jan123', now(), '11-111', 'Poland', 'Warsaw', 'Firststreet', 20);
INSERT INTO contacts (id, version, is_archival, created_by, created_at, first_name, last_name, address_id)
    VALUES (nextval('contacts_seq'), 0, false, 'jan123', now(), 'Jan', 'Kowalski', (SELECT id FROM addresses WHERE postal_code = '11-111'));
INSERT INTO accounts (id, version, is_archival, created_by, created_at, login, email, password, locale, contact_id, state, roles, unsuccessful_auth_counter)
    VALUES (nextval('accounts_seq'), 0, false, 'jan123', now(), 'jan123', 'jan.kowalski@example.com', '$2a$12$Pnhm6JI7c2l0h102DMVNF.Pu0dNWHxtWgJU0zd6OkbNvcolrLMoo2', 'pl',
            (SELECT id FROM contacts WHERE first_name = 'Jan'), 'ACTIVE', '{CLIENT}', 0);

INSERT INTO addresses (id, version, is_archival, created_by, created_at, postal_code, country, city, street, house_number)
    VALUES (nextval('addresses_seq'), 0, false, 'michal123', now(), '22-222', 'Poland', 'Warsaw', 'Secondstreet', 10);
INSERT INTO contacts (id, version, is_archival, created_by, created_at, first_name, last_name, address_id)
    VALUES (nextval('contacts_seq'), 0, false, 'michal123', now(), 'Michał', 'Nowak', (SELECT id FROM addresses WHERE postal_code = '22-222'));
INSERT INTO accounts (id, version, is_archival, created_by, created_at, login, email, password, locale, contact_id, state, roles, unsuccessful_auth_counter)
    VALUES (nextval('accounts_seq'), 0, false, 'michal123', now(), 'michal123', 'michal.nowak@example.com', '$2a$12$4138d5.Du5XhnlriUSZKDeFsLLIr.cBveHeAuGgQ1qB4fOnwo/dfa', 'pl',
            (SELECT id FROM contacts WHERE first_name = 'Michał'), 'ACTIVE', '{ADMIN}', 0);

INSERT INTO addresses (id, version, is_archival, created_by, created_at, postal_code, country, city, street, house_number)
    VALUES (nextval('addresses_seq'), 0, false, 'john123', now(), '33-333', 'England', 'London', 'Thirdstreet', 10);
INSERT INTO contacts (id, version, is_archival, created_by, created_at, first_name, last_name, address_id)
    VALUES (nextval('contacts_seq'), 0, false, 'john123', now(), 'John', 'Doe', (SELECT id FROM addresses WHERE postal_code = '33-333'));
INSERT INTO accounts (id, version, is_archival, created_by, created_at, login, email, password, locale, contact_id, state, roles, unsuccessful_auth_counter)
    VALUES (nextval('accounts_seq'), 0, false, 'john123', now(), 'john123', 'john.doe@example.com', '$2a$12$E3okB8Yo8ZprzIG9gMNZfedjIO2j7V8LpYVzIp0EhGeHBweYXS6fS', 'en',
            (SELECT id FROM contacts WHERE first_name = 'John'), 'ACTIVE', '{EMPLOYEE}', 0);
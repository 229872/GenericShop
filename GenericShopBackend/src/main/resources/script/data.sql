INSERT INTO addresses (id, version, is_archival, created_by, created_at, postal_code, country, city, street, house_number)
    VALUES (nextval('addresses_seq'), 0, false, 'jan123', now(), '11-111', 'Poland', 'Warsaw', 'Firststreet', 20)
    ON CONFLICT DO NOTHING;
INSERT INTO contacts (id, version, is_archival, created_by, created_at, first_name, last_name, address_id)
    VALUES (nextval('contacts_seq'), 0, false, 'jan123', now(), 'Jan', 'Kowalski', (SELECT id FROM addresses WHERE postal_code = '11-111'))
    ON CONFLICT DO NOTHING;
INSERT INTO accounts (id, version, is_archival, created_by, created_at, login, email, password, locale, contact_id, state, roles, unsuccessful_auth_counter)
    VALUES (nextval('accounts_seq'), 0, false, 'jan123', now(), 'jan123', 'jan.kowalski@example.com', '$2a$12$Pnhm6JI7c2l0h102DMVNF.Pu0dNWHxtWgJU0zd6OkbNvcolrLMoo2', 'pl',
            (SELECT id FROM contacts WHERE first_name = 'Jan'), 'ACTIVE', '{ADMIN}', 0)
    ON CONFLICT DO NOTHING;

INSERT INTO addresses (id, version, is_archival, created_by, created_at, postal_code, country, city, street, house_number)
    VALUES (nextval('addresses_seq'), 0, false, 'michal123', now(), '22-222', 'Poland', 'Warsaw', 'Secondstreet', 10)
    ON CONFLICT DO NOTHING;
INSERT INTO contacts (id, version, is_archival, created_by, created_at, first_name, last_name, address_id)
    VALUES (nextval('contacts_seq'), 0, false, 'michal123', now(), 'Michał', 'Nowak', (SELECT id FROM addresses WHERE postal_code = '22-222'))
    ON CONFLICT DO NOTHING;
INSERT INTO accounts (id, version, is_archival, created_by, created_at, login, email, password, locale, contact_id, state, roles, unsuccessful_auth_counter)
    VALUES (nextval('accounts_seq'), 0, false, 'michal123', now(), 'michal123', 'michal.nowak@example.com', '$2a$12$4138d5.Du5XhnlriUSZKDeFsLLIr.cBveHeAuGgQ1qB4fOnwo/dfa', 'pl',
            (SELECT id FROM contacts WHERE first_name = 'Michał'), 'ACTIVE', '{CLIENT}', 0)
    ON CONFLICT DO NOTHING;

INSERT INTO addresses (id, version, is_archival, created_by, created_at, postal_code, country, city, street, house_number)
    VALUES (nextval('addresses_seq'), 0, false, 'john123', now(), '33-333', 'England', 'London', 'Thirdstreet', 10)
    ON CONFLICT DO NOTHING;
INSERT INTO contacts (id, version, is_archival, created_by, created_at, first_name, last_name, address_id)
    VALUES (nextval('contacts_seq'), 0, false, 'john123', now(), 'John', 'Doe', (SELECT id FROM addresses WHERE postal_code = '33-333'))
    ON CONFLICT DO NOTHING;
INSERT INTO accounts (id, version, is_archival, created_by, created_at, login, email, password, locale, contact_id, state, roles, unsuccessful_auth_counter)
    VALUES (nextval('accounts_seq'), 0, false, 'john123', now(), 'john123', 'john.doe@example.com', '$2a$12$E3okB8Yo8ZprzIG9gMNZfedjIO2j7V8LpYVzIp0EhGeHBweYXS6fS', 'en',
            (SELECT id FROM contacts WHERE first_name = 'John'), 'ACTIVE', '{EMPLOYEE}', 0)
    ON CONFLICT DO NOTHING;

INSERT INTO addresses (id, version, is_archival, created_by, created_at, postal_code, country, city, street, house_number)
    VALUES (nextval('addresses_seq'), 0, false, 'bartek123', now(), '77-333', 'Poland', 'Lodz', 'Wólczańska', 900)
    ON CONFLICT DO NOTHING;
INSERT INTO contacts (id, version, is_archival, created_by, created_at, first_name, last_name, address_id)
    VALUES (nextval('contacts_seq'), 0, false, 'bartek123', now(), 'Bartek', 'Doe', (SELECT id FROM addresses WHERE postal_code = '77-333'))
    ON CONFLICT DO NOTHING;
INSERT INTO accounts (id, version, is_archival, created_by, created_at, login, email, password, locale, contact_id, state, roles, unsuccessful_auth_counter)
    VALUES (nextval('accounts_seq'), 0, false, 'bartek123', now(), 'bartek123', 'bartek.doe@example.com', '$2a$12$E3okB8Yo8ZprzIG9gMNZfedjIO2j7V8LpYVzIp0EhGeHBweYXS6fS', 'en',
        (SELECT id FROM contacts WHERE first_name = 'Bartek'), 'ACTIVE', '{EMPLOYEE,CLIENT}', 0)
    ON CONFLICT DO NOTHING;

INSERT INTO categories (id, version, created_by, created_at, name)
    VALUES (nextval('categories_seq'), 0, 'john123', now(), 'Yerba'),
           (nextval('categories_seq'), 0, 'john123', now(), 'Book'),
           (nextval('categories_seq'), 0, 'john123', now(), 'Game'),
           (nextval('categories_seq'), 0, 'john123', now(), 'Tv')
    ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS yerbas (
    product_id BIGINT PRIMARY KEY,
    net_mass_in_grams INTEGER NOT NULL,
    power_level INTEGER NOT NULL,
    dust_amount INTEGER NOT NULL,
    bitterness_level INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS books (
    product_id BIGINT PRIMARY KEY,
    author_name VARCHAR(255) NOT NULL,
    number_of_pages INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS games (
    product_id BIGINT PRIMARY KEY,
    genre VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tvs (
    product_id BIGINT PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    color VARCHAR(255) NOT NULL,
    screen VARCHAR(255) NOT NULL
);

INSERT INTO products (id, version, is_archival, created_by, created_at, name, price, quantity, image_url, category_id)
     VALUES (nextval('products_seq'), 0, false, 'john123', now(), 'SAMSUNG QE65Q67C 65" QLED 4K Tizen TV', 999.99, 4, null, (SELECT id FROM categories WHERE name='Tv')),
            (nextval('products_seq'), 0, false, 'john123', now(), 'Canarias', 30.00, 29, null, (SELECT id FROM categories WHERE name='Yerba')),
            (nextval('products_seq'), 0, false, 'john123', now(), 'Pipore Hierbas', 25.00, 8, null, (SELECT id FROM categories WHERE name='Yerba')),
            (nextval('products_seq'), 0, false, 'john123', now(), 'Pipore Sublime', 25.00, 12, null, (SELECT id FROM categories WHERE name='Yerba')),
            (nextval('products_seq'), 0, true, 'john123', now(), 'Pajarito', 24.99, 3, null, (SELECT id FROM categories WHERE name='Yerba')),
            (nextval('products_seq'), 0, false, 'john123', now(), 'PHILIPS 55PML9008 55" MINILED 4K 120Hz Ambilight 3 Dolby Atmos Dolby Vision HDMI 2.1', 777.05, 2, null, (SELECT id FROM categories WHERE name='Tv')),
            (nextval('products_seq'), 0, false, 'john123', now(), 'The Witcher 3 Wild Hunt', 46.99, 20, null, (SELECT id FROM categories WHERE name='Game')),
            (nextval('products_seq'), 0, false, 'john123', now(), 'Java Techniki zaawansowane', 258.99, 3, null, (SELECT id FROM categories WHERE name='Book')),
            (nextval('products_seq'), 0, true, 'john123', now(), 'The Lord of the Rings: The Two Towers', 40.99, 1, null, (SELECT id FROM categories WHERE name='Book')),
            (nextval('products_seq'), 0, false, 'john123', now(), 'Linux. Biblia', 99.99, 3, null, (SELECT id FROM categories WHERE name='Book')),
            (nextval('products_seq'), 0, true, 'john123', now(), 'Spring w akcji', 110.99, 9, null, (SELECT id FROM categories WHERE name='Book'))
    ON CONFLICT DO NOTHING;

INSERT INTO yerbas (net_mass_in_grams, power_level, dust_amount, bitterness_level, product_id)
    VALUES (1000, 4, 4, 4, (SELECT id FROM products WHERE name='Canarias')),
           (500, 3, 2, 3, (SELECT id FROM products WHERE name='Pipore Hierbas')),
           (500, 2, 2, 3, (SELECT id FROM products WHERE name='Pipore Sublime')),
           (500, 4, 3, 3, (SELECT id FROM products WHERE name='Pajarito'))
    ON CONFLICT DO NOTHING;

INSERT INTO books (author_name, number_of_pages, product_id)
    VALUES ('Cay S. Horstmann', 808, (SELECT id FROM products WHERE name='Java Techniki zaawansowane')),
           ('J. R. R. Tolkien', 352, (SELECT id FROM products WHERE name='The Lord of the Rings: The Two Towers')),
           ('Negus Christopher', 872, (SELECT id FROM products WHERE name='Linux. Biblia')),
           ('Walls Craig', 408, (SELECT id FROM products WHERE name='Spring w akcji'))
    ON CONFLICT DO NOTHING;

INSERT INTO games (genre, product_id)
    VALUES ('adventure game', (SELECT ID FROM products WHERE name='The Witcher 3 Wild Hunt'))
    ON CONFLICT DO NOTHING;

INSERT INTO tvs (brand, color, screen, product_id)
    VALUES ('Samsung', 'black', '65" QLED, UHD/4K, 3840 x 2160px', (SELECT id FROM products WHERE name='SAMSUNG QE65Q67C 65" QLED 4K Tizen TV')),
           ('Philips', 'black', '55" MINILED, UHD/4K, 3840 x 2160px', (SELECT id FROM products WHERE name='PHILIPS 55PML9008 55" MINILED 4K 120Hz Ambilight 3 Dolby Atmos Dolby Vision HDMI 2.1'))
    ON CONFLICT DO NOTHING;
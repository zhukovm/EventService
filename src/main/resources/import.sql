INSERT INTO known_fruits(id, name) VALUES (nextval('known_fruits_id_seq'), 'Cherry');
INSERT INTO known_fruits(id, name) VALUES (nextval('known_fruits_id_seq'), 'Apple');
INSERT INTO known_fruits(id, name) VALUES (nextval('known_fruits_id_seq'), 'Banana');


--INSERT INTO roles(id, name) VALUES ('10f1a89d-193c-4f9b-b420-55c7d2aaf708', 'Administrator');
--INSERT INTO roles(id, name) VALUES ('10f1a89d-193c-4f9b-b420-55c7d2aaf709', 'User');

INSERT INTO users(id, birthdate, email, firstname, isemailverified, lastname, preferredUsername, patronymic, phone)
VALUES ('10f1a89d-193c-4f9b-b420-55c7d2aaf710', '12/01/1990', 'EventServiceUser@yandex.ru', 'EventServiceUser', false, 'Service', 'EventServiceUser','User', '89272126275');
INSERT INTO version VALUES (0);

INSERT INTO day VALUES (1, '25 de Julio', 60);
INSERT INTO day VALUES (2, '5 de Agosto', 60);
INSERT INTO day VALUES (3, '6 de Agosto', 60);
INSERT INTO day VALUES (4, '7 de Agosto', 50);
INSERT INTO day VALUES (5, '8 de Agosto', 50);
INSERT INTO day VALUES (6, '9 de Agosto', 50);

INSERT INTO offer VALUES (1, 'Pack tres dias a elegir', 3, 150);
INSERT INTO offer VALUES (2, 'Pack fiestas sin Santiago', 5, 240);
INSERT INTO offer VALUES (3, 'Pack fiestas + Santiago', 6, 270);

INSERT INTO user VALUES (1, 'Valentin', 'Inigo', 'Valentin', 'i@inigovalentin.com', '620040802'); 
INSERT INTO user VALUES (2, 'Txapata', 'Aitor', 'Gonzalez', null, null); 
INSERT INTO user VALUES (3, 'Araiz', 'Araiz', 'Zarain', null, '695732552'); 
INSERT INTO user VALUES (4, 'Puma', 'Alberto', 'Esteban', null, null); 
INSERT INTO user VALUES (5, null, null, null, null, null);

INSERT INTO place VALUES (1, 'Virgen Blanca', 'Plaza de la virgen blanca', '01001', 42.846484, -2.673625);
INSERT INTO place VALUES (2, 'Plaza de los Fueros', 'Plaza de los Fueros', '01005', 42.843056, -2.670110);
INSERT INTO place VALUES (3, 'Restaurante Silver', 'Calle Fueros, 20', '01005', 42.843469, -2.670187);
INSERT INTO place VALUES (4, 'Calle Dato', 'Calle Eduardo Dato, 1', '01005', 42.845943, -2.672284);
INSERT INTO place VALUES (5, 'Iradier Arena', 'Calle Florida, 78', '01004', 42.841107, -2.665128);
INSERT INTO place VALUES (6, 'Dominos Pizza', 'Avenida Gasteiz, 24', '01004', 42.845254, -2.682060);

INSERT INTO people VALUES (1, 'Gasteizko Margolariak', 'http://margolariak.com');
INSERT INTO people VALUES (2, 'Ayuntamiento de Vitoria', 'http://www.vitoria-gasteiz.org');

INSERT INTO event VALUES (1, 1, 1, 'Test event gm-s', 'This is a schedule and gm event to test the app', 1, 1, str_to_date('2015-07-25 12:00:00', '%Y-%m-%d %H:%i:%s'), null);
INSERT INTO event VALUES (2, 0, 1, 'Test event gm', 'This is a gm event to test the app', 1, 1, str_to_date('2015-07-25 13:00:00', '%Y-%m-%d %H:%i:%s'), null);
INSERT INTO event VALUES (3, 1, 0, 'Test event s', 'This is a s event to test the app', 1, 1, str_to_date('2015-07-25 14:00:00', '%Y-%m-%d %H:%i:%s'), null);

INSERT INTO event VALUES (4, 1, 0, 'Celedon', 'Bajada de celedon', 1, 1, str_to_date('2015-08-04 16:00:00', '%Y-%m-%d %H:%i:%s'), null);
INSERT INTO event VALUES (5, 0, 1, 'Almuerzo', 'Bajada de celedon', 1, 1, str_to_date('2015-08-05 11:00:00', '%Y-%m-%d %H:%i:%s'), null);

INSERT INTO event VALUES (6, 0, 1, 'Almuerzo', 'Almuerzo de cuadrilla para coger fueras.', 1, 2, str_to_date('2015-07-25 10:00:00', '%Y-%m-%d %H:%i:%s'), null);
INSERT INTO event VALUES (7, 0, 1, 'Comida', 'Comida con la cuadrilla.', 1, 3, str_to_date('2015-07-25 14:00:00', '%Y-%m-%d %H:%i:%s'), null);
INSERT INTO event VALUES (8, 1, 1, 'Paseillo de ida', 'Desfile de cuadrillas desde el centro a la plaza de toros.', 2, 4, str_to_date('2015-07-25 16:00:00', '%Y-%m-%d %H:%i:%s'), null);
INSERT INTO event VALUES (9, 1, 1, 'Paseillo de vuelta', 'Desfile de cuadrillas desde la plaza de toros hasta el centro.', 2, 5, str_to_date('2015-07-25 20:00:00', '%Y-%m-%d %H:%i:%s'), null);
INSERT INTO event VALUES (10, 1, 1, 'Cena con la cuadrilla', 'Despues de un dia duro, unas pizzas para reponer energias.', 1, 6, str_to_date('2015-07-25 22:30:00', '%Y-%m-%d %H:%i:%s'), null);


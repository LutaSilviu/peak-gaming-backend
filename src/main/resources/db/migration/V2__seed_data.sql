INSERT INTO station_types (code, short_name, full_name, people_per_unit, max_people) VALUES
    ('pc',    'PC',         'Calculator de gaming', 1, 9),
    ('ps5',   'PlayStation','PlayStation 5',         2, 10),
    ('volan', 'Volan',      'Postul de curse',       1, 1);

INSERT INTO stations (code, station_type, display_order) VALUES
    ('PC1', 'pc', 1), ('PC2', 'pc', 2), ('PC3', 'pc', 3),
    ('PC4', 'pc', 4), ('PC5', 'pc', 5), ('PC6', 'pc', 6),
    ('PC7', 'pc', 7), ('PC8', 'pc', 8), ('PC9', 'pc', 9),
    ('PS1', 'ps5', 1), ('PS2', 'ps5', 2), ('PS3', 'ps5', 3),
    ('PS4', 'ps5', 4), ('PS5', 'ps5', 5),
    ('VOL', 'volan', 1);

-- PC: standard rate, no student discount tier
INSERT INTO price_tiers (station_type, student_rate, duration_hours, price_lei) VALUES
    ('pc', FALSE, 1, 12), ('pc', FALSE, 2, 22), ('pc', FALSE, 3, 30), ('pc', FALSE, 4, 38),
    ('pc', FALSE, 5, 45), ('pc', FALSE, 6, 50), ('pc', FALSE, 8, 60), ('pc', FALSE, 12, 90);

-- PS5: standard and student rates
INSERT INTO price_tiers (station_type, student_rate, duration_hours, price_lei) VALUES
    ('ps5', FALSE, 1, 30), ('ps5', FALSE, 2, 55), ('ps5', FALSE, 3, 75), ('ps5', FALSE, 5, 115),
    ('ps5', TRUE,  1, 25), ('ps5', TRUE,  2, 45), ('ps5', TRUE,  3, 60), ('ps5', TRUE,  5, 90);

-- volan: billed at the PS5 rate (no student tier distinction in the room, but
-- the frontend applies the PS5 table verbatim, including the student rate)
INSERT INTO price_tiers (station_type, student_rate, duration_hours, price_lei) VALUES
    ('volan', FALSE, 1, 30), ('volan', FALSE, 2, 55), ('volan', FALSE, 3, 75), ('volan', FALSE, 5, 115),
    ('volan', TRUE,  1, 25), ('volan', TRUE,  2, 45), ('volan', TRUE,  3, 60), ('volan', TRUE,  5, 90);

INSERT INTO zones (code, label, tag, title, description, capacity_label, hours_label, display_order) VALUES
    ('pc', 'PC-uri', 'ZONA PC', 'Nouă calculatoare de gaming',
     'Un singur rând de-a lungul peretelui din dreapta, cu periferice de gaming la fiecare post. CS2, Valorant, League of Legends, Fortnite și orice altceva instalezi. Te loghezi pe conturile tale și continui de unde ai rămas.',
     '9 posturi', 'de la 12 lei/h', 1),
    ('ps5', 'PlayStation 5', 'ZONA PLAYSTATION', 'Cinci pod-uri PS5',
     'Pe partea stângă, cinci pod-uri cu ecran mare, canapea și două controllere. Prețul e pe stație, nu pe persoană — vii singur sau în doi, costă la fel. FC 26, NBA 2K25, UFC 5, Mortal Kombat, It Takes Two.',
     '5 stații', 'de la 25 lei/h', 2),
    ('volan', 'Curse', 'POSTUL DE CURSE', 'Volan cu pedale',
     'În fundul sălii, la mijloc, chiar sub siglă. Volan cu pedale și ecran propriu. Forza Horizon 5, Assetto Corsa Competizione, Dirt 5, WRC și Need for Speed Heat.',
     '1 post', 'tarif PS5', 3),
    ('bar', 'Bar', 'BAR', 'Gustări și băuturi',
     'Chiar la intrare, în mijlocul sălii. Frigidere cu băuturi reci, gustări și trei scaune înalte. Nu trebuie să ieși din sală în mijlocul unei partide.',
     'La intrare', '12–24', 4);

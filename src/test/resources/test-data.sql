-- Очистка существующих данных
TRUNCATE TABLE books_genres, books, authors, genres CASCADE;

-- Вставка тестовых авторов
INSERT INTO authors (id, first_name, last_name, biography) VALUES
                                                               (1, 'Александр', 'Пушкин', 'Великий русский поэт и писатель'),
                                                               (2, 'Лев', 'Толстой', 'Русский писатель, классик мировой литературы'),
                                                               (3, 'Федор', 'Достоевский', 'Русский писатель, мыслитель и публицист');

-- Вставка тестовых жанров
INSERT INTO genres (id, name, description) VALUES
                                               (1, 'Роман', 'Литературный жанр большой формы'),
                                               (2, 'Поэзия', 'Стихотворные произведения'),
                                               (3, 'Драма', 'Литературно-драматический жанр');

-- Вставка тестовых книг
INSERT INTO books (id, title, isbn, publication_year, author_id) VALUES
                                                                     (1, 'Евгений Онегин', '978-5-17-123456-1', 1833, 1),
                                                                     (2, 'Война и мир', '978-5-17-123456-2', 1869, 2),
                                                                     (3, 'Преступление и наказание', '978-5-17-123456-3', 1866, 3);

-- Связи между книгами и жанрами
INSERT INTO books_genres (book_id, genre_id) VALUES
                                                 (1, 1), -- Евгений Онегин - Роман
                                                 (1, 2), -- Евгений Онегин - Поэзия
                                                 (2, 1), -- Война и мир - Роман
                                                 (3, 1), -- Преступление и наказание - Роман
                                                 (3, 3); -- Преступление и наказание - Драма

-- Сброс последовательностей
SELECT setval('authors_id_seq', (SELECT MAX(id) FROM authors));
SELECT setval('books_id_seq', (SELECT MAX(id) FROM books));
SELECT setval('genres_id_seq', (SELECT MAX(id) FROM genres));
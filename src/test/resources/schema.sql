-- Удаляем таблицы, если они существуют
DROP TABLE IF EXISTS books_genres CASCADE;
DROP TABLE IF EXISTS books CASCADE;
DROP TABLE IF EXISTS authors CASCADE;
DROP TABLE IF EXISTS genres CASCADE;

-- Создание таблицы авторов
CREATE TABLE authors (
                         id BIGSERIAL PRIMARY KEY,
                         first_name VARCHAR(100) NOT NULL,
                         last_name VARCHAR(100) NOT NULL,
                         biography TEXT,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы жанров
CREATE TABLE genres (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL UNIQUE,
                        description TEXT,
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы книг
CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       isbn VARCHAR(20) UNIQUE,
                       publication_year INTEGER,
                       author_id BIGINT NOT NULL,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_author
                           FOREIGN KEY (author_id)
                               REFERENCES authors (id)
                               ON DELETE CASCADE
);

-- Создание связующей таблицы для many-to-many между книгами и жанрами
CREATE TABLE books_genres (
                              book_id BIGINT NOT NULL,
                              genre_id BIGINT NOT NULL,
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (book_id, genre_id),
                              CONSTRAINT fk_book
                                  FOREIGN KEY (book_id)
                                      REFERENCES books (id)
                                      ON DELETE CASCADE,
                              CONSTRAINT fk_genre
                                  FOREIGN KEY (genre_id)
                                      REFERENCES genres (id)
                                      ON DELETE CASCADE
);

-- Создание индексов
CREATE INDEX idx_authors_last_name ON authors(last_name);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_author_id ON books(author_id);
CREATE INDEX idx_genres_name ON genres(name);

-- Создание триггера для обновления updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- Применяем триггер к таблицам
CREATE TRIGGER update_authors_updated_at
    BEFORE UPDATE ON authors
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_books_updated_at
    BEFORE UPDATE ON books
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_genres_updated_at
    BEFORE UPDATE ON genres
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
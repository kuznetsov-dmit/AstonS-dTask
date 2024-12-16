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

-- Создание таблицы книг с внешним ключом на автора
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

-- Создание связующей таблицы для связи many-to-many между книгами и жанрами
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

-- Индексы для оптимизации производительности
CREATE INDEX idx_authors_last_name ON authors(last_name);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_author_id ON books(author_id);
CREATE INDEX idx_genres_name ON genres(name);

-- Триггер для обновления updated_at в таблице authors
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_authors_updated_at
    BEFORE UPDATE ON authors
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Триггер для обновления updated_at в таблице books
CREATE TRIGGER update_books_updated_at
    BEFORE UPDATE ON books
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Триггер для обновления updated_at в таблице genres
CREATE TRIGGER update_genres_updated_at
    BEFORE UPDATE ON genres
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Комментарии к таблицам и колонкам для документации
COMMENT ON TABLE authors IS 'Таблица авторов книг';
COMMENT ON COLUMN authors.first_name IS 'Имя автора';
COMMENT ON COLUMN authors.last_name IS 'Фамилия автора';
COMMENT ON COLUMN authors.biography IS 'Биография автора';

COMMENT ON TABLE books IS 'Таблица книг';
COMMENT ON COLUMN books.title IS 'Название книги';
COMMENT ON COLUMN books.isbn IS 'Международный стандартный книжный номер';
COMMENT ON COLUMN books.publication_year IS 'Год публикации книги';
COMMENT ON COLUMN books.author_id IS 'Идентификатор автора книги';

COMMENT ON TABLE genres IS 'Таблица жанров книг';
COMMENT ON COLUMN genres.name IS 'Название жанра';
COMMENT ON COLUMN genres.description IS 'Описание жанра';

COMMENT ON TABLE books_genres IS 'Связующая таблица между книгами и жанрами';
package mapper;

import dto.BookDto;

import entity.Book;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;



@Mapper
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookDto toDto(Book book);
    Book toEntity(BookDto bookDto);
}

package mapper;

import dto.AuthorDto;
import entity.Author;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthorMapper {
    AuthorMapper INSTANCE = Mappers.getMapper(AuthorMapper.class);

    AuthorDto toDto(Author author);
    Author toEntity(AuthorDto authorDto);
}

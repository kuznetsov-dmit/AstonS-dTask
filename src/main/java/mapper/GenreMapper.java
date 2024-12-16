package mapper;

import dto.GenreDto;
import entity.Genre;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GenreMapper {
    GenreMapper INSTANCE = Mappers.getMapper(GenreMapper.class);

    GenreDto toDto(Genre genre);
    Genre toEntity(GenreDto genreDto);
}

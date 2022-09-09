package de.kaufland.moviesearch.indexservice.searchservice.converter;

import de.kaufland.moviesearch.indexservice.searchservice.model.dto.MovieDto;
import de.kaufland.moviesearch.indexservice.searchservice.model.elasticsearch.MovieModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MovieModelToSearchResultModelConverter implements Converter<MovieModel, MovieDto> {
    @Override
    public MovieDto convert(MovieModel source) {
        MovieDto movieDto = MovieDto.builder()
                .id(source.getId())
                .title(source.getTitle())
                .year(source.getYear())
                .runtime(source.getRuntime())
                .genres(source.getGenres())
                .director(source.getDirector())
                .actors(source.getActors())
                .plot(source.getPlot())
                .posterUrl(source.getPosterUrl())
                .build();
        return movieDto;
    }
}

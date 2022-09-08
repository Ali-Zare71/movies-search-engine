package de.kaufland.moviesearch.indexservice.searchservice.converter;

import de.kaufland.moviesearch.indexservice.searchservice.model.dto.SearchResultDto;
import de.kaufland.moviesearch.indexservice.searchservice.model.elasticsearch.MovieModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MovieModelToSearchResultModelConverter implements Converter<MovieModel, SearchResultDto> {
    @Override
    public SearchResultDto convert(MovieModel source) {
        SearchResultDto searchResultDto = SearchResultDto.builder()
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
        return searchResultDto;
    }
}

package de.kaufland.moviesearch.indexservice.converter;

import de.kaufland.moviesearch.indexservice.model.index.IndexMovieModel;
import de.kaufland.moviesearch.indexservice.model.rabbitmq.MovieModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MovieModelToIndexMovieModelConverter implements Converter<MovieModel, IndexMovieModel> {

    @Override
    public IndexMovieModel convert(MovieModel source) {
        return IndexMovieModel.builder().id(source.getId())
                .title(source.getTitle())
                .year(source.getYear())
                .runtime(source.getRuntime())
                .genres(source.getGenres())
                .actors(source.getActors())
                .plot(source.getPlot())
                .posterUrl(source.getPosterUrl())
                .build();
    }
}

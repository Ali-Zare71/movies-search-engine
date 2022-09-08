package de.kaufland.moviesearch.indexservice.converter;

import de.kaufland.moviesearch.indexservice.model.index.IndexMovieModel;
import de.kaufland.moviesearch.indexservice.model.rabbitmq.MovieModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MovieModelToIndexMovieModelConverter implements Converter<MovieModel, IndexMovieModel> {

    @Override
    public IndexMovieModel convert(MovieModel source) {
        List<String> parsedActors = new ArrayList<>();
        if (!StringUtils.isEmpty(source.getActors())) {
            Arrays.stream(source.getActors().split(",")).forEach(s -> parsedActors.add(s.trim()));
        }
        return IndexMovieModel.builder().id(source.getId())
                .title(source.getTitle())
                .year(source.getYear())
                .runtime(source.getRuntime())
                .genres(source.getGenres())
                .director(source.getDirector())
                .parsedActors(parsedActors)
                .actors(source.getActors())
                .plot(source.getPlot())
                .posterUrl(source.getPosterUrl())
                .build();
    }
}

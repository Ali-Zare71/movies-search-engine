package de.kaufland.moviesearch.indexservice.service;

import de.kaufland.moviesearch.indexservice.converter.MovieModelToIndexMovieModelConverter;
import de.kaufland.moviesearch.indexservice.model.index.IndexMovieModel;
import de.kaufland.moviesearch.indexservice.model.rabbitmq.MovieModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StoreService {
    //    private final ConversionService conversionService;
    private final ElasticsearchService elasticsearchService;
    private final MovieModelToIndexMovieModelConverter movieModelToIndexMovieModelConverter;
    @Value("${index.name}")
    private String indexName;

    public StoreService(ElasticsearchService elasticsearchService, MovieModelToIndexMovieModelConverter movieModelToIndexMovieModelConverter) {

        this.elasticsearchService = elasticsearchService;
        this.movieModelToIndexMovieModelConverter = movieModelToIndexMovieModelConverter;
    }

    /**
     * convert rabbitmq model to elasticsearch model and stores the result to elasticsearch
     *
     * @param movieModel
     */
    public void storeDocument(MovieModel movieModel) {
        IndexMovieModel model = movieModelToIndexMovieModelConverter.convert(movieModel);
        elasticsearchService.storeDocument(model, indexName);
    }
}

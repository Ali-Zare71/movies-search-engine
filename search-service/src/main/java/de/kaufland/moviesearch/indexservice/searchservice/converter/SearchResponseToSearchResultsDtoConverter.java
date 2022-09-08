package de.kaufland.moviesearch.indexservice.searchservice.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kaufland.moviesearch.indexservice.searchservice.model.dto.SearchResultDto;
import de.kaufland.moviesearch.indexservice.searchservice.model.dto.SearchResultsDto;
import de.kaufland.moviesearch.indexservice.searchservice.model.elasticsearch.MovieModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

public class SearchResponseToSearchResultsDtoConverter implements Converter<SearchResponse, SearchResultsDto> {
    private static final Logger LOGGER = LogManager.getLogger(SearchResponseToSearchResultsDtoConverter.class.getName());
    private final ObjectMapper objectMapper;
    private final MovieModelToSearchResultModelConverter movieModelToSearchResultModelConverter = new MovieModelToSearchResultModelConverter();

    public SearchResponseToSearchResultsDtoConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public SearchResultsDto convert(SearchResponse source) {
        SearchResultsDto<SearchResultDto> results = new SearchResultsDto();
        try {
            if (source == null) {
                results.setTotal(-1);
                return results;
            }
            results.setTime(source.getTook().getMillis());
            results.setTotal(source.getHits().getTotalHits().value);
            SearchHit[] searchHits = source.getHits().getHits();
            for (SearchHit searchHit : searchHits) {
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                MovieModel movieModel = objectMapper.convertValue(sourceAsMap, MovieModel.class);
                results.getResults().add(movieModelToSearchResultModelConverter.convert(movieModel));
            }
        } catch (Exception e) {
            LOGGER.catching(e);
        }
        return results;
    }
}

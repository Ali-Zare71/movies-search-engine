package de.kaufland.moviesearch.indexservice.searchservice.service;

import de.kaufland.moviesearch.indexservice.searchservice.model.dto.SearchResultsDto;
import de.kaufland.moviesearch.indexservice.searchservice.model.elasticsearch.ElasticsearchFields;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    private static final Logger LOGGER = LogManager.getLogger(SearchService.class.getName());
    public final ConversionService conversionService;
    private final ElasticsearchService elasticsearchService;
    @Value("${index.name}")
    private String indexName;

    public SearchService(ElasticsearchService elasticsearchService, ConversionService conversionService) {
        this.elasticsearchService = elasticsearchService;
        this.conversionService = conversionService;
    }

    public SearchResultsDto search(String query, int page, int size) {
        try {
            QueryBuilder queryBuilder = createQuery(query);
            SearchRequest searchRequest = createSearchRequest(queryBuilder, page, size);
            SearchResponse searchResponse = elasticsearchService.executeQuery(searchRequest);
            SearchResultsDto searchResultsDto = conversionService.convert(searchResponse, SearchResultsDto.class);
            return searchResultsDto;
        } catch (Exception e) {
            LOGGER.catching(e);
        }
        return new SearchResultsDto();
    }

    /**
     * creates elasticsearch query by user query
     *
     * @param query
     * @return
     */
    private QueryBuilder createQuery(String query) {
        BoolQueryBuilder result = QueryBuilders.boolQuery();
        result.should(QueryBuilders.matchPhraseQuery(ElasticsearchFields.TITLE.getFieldName(), query).slop(50).boost(2));
        result.should(QueryBuilders.matchPhraseQuery(ElasticsearchFields.ACTORS.getFieldName(), query).slop(50));
        return null;
    }

    /**
     * creates elasticsearch search request.
     *
     * @param query
     * @param page
     * @param size
     * @return
     */
    private SearchRequest createSearchRequest(QueryBuilder query, int page, int size) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchSourceBuilder.from((page - 1) * size);
        searchSourceBuilder.size(size);
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

}

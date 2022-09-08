package de.kaufland.moviesearch.indexservice.searchservice.service;

import de.kaufland.moviesearch.indexservice.searchservice.model.dto.SearchResultsDto;
import de.kaufland.moviesearch.indexservice.searchservice.model.elasticsearch.ElasticsearchFields;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
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
    @Value("${title.boost}")
    private float titleBoost;
    @Value("${actors.boost}")
    private float actorsBoost;
    @Value("${fuzzy.query.boost}")
    private float fuzzyQueryBoost;
    @Value("${search.slop}")
    private int searchSlop;
    @Value("${minimum.should.match}")
    private String minimumShouldMatch;
    @Value("${fuzziness}")
    private String fuzziness;


    public SearchService(ElasticsearchService elasticsearchService, ConversionService conversionService) {
        this.elasticsearchService = elasticsearchService;
        this.conversionService = conversionService;
    }

    public SearchResultsDto search(String query, int page, int size, boolean fuzzySearch, boolean orQuery) {
        try {
            QueryBuilder queryBuilder = createQuery(query, fuzzySearch, orQuery);
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

    private BoolQueryBuilder createQuery(String query, boolean fuzzySearch, boolean orQuery) {
        BoolQueryBuilder result = QueryBuilders.boolQuery();
        if (!orQuery) {
            result.should(QueryBuilders.matchPhraseQuery(ElasticsearchFields.TITLE.getFieldName(), query).boost(titleBoost).slop(searchSlop));
            result.should(QueryBuilders.matchPhraseQuery(ElasticsearchFields.PARSED_ACTORS.getFieldName(), query).boost(actorsBoost).slop(searchSlop));
        } else {
            result.should(QueryBuilders.matchQuery(ElasticsearchFields.TITLE.getFieldName(), query).boost(titleBoost).operator(Operator.OR).fuzziness(Fuzziness.ZERO).minimumShouldMatch(minimumShouldMatch));
            result.should(QueryBuilders.matchQuery(ElasticsearchFields.PARSED_ACTORS.getFieldName(), query).boost(actorsBoost).operator(Operator.OR).fuzziness(Fuzziness.ZERO).minimumShouldMatch(minimumShouldMatch));
        }
        if (fuzzySearch) {
            result.should(QueryBuilders.matchQuery(ElasticsearchFields.TITLE.getFieldName(), query).boost(titleBoost * fuzzyQueryBoost).operator(Operator.AND).fuzziness(Fuzziness.build(fuzziness)));
            result.should(QueryBuilders.matchQuery(ElasticsearchFields.PARSED_ACTORS.getFieldName(), query).boost(actorsBoost * fuzzyQueryBoost).operator(Operator.AND).fuzziness(Fuzziness.build(fuzziness)));
        }
        return result;
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

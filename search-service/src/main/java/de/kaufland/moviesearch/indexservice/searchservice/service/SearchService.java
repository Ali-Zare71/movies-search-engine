package de.kaufland.moviesearch.indexservice.searchservice.service;

import de.kaufland.moviesearch.indexservice.searchservice.model.dto.SearchResultDto;
import de.kaufland.moviesearch.indexservice.searchservice.model.elasticsearch.ElasticsearchFields;
import de.kaufland.moviesearch.indexservice.searchservice.model.exception.InternalServerErrorException;
import de.kaufland.moviesearch.indexservice.searchservice.model.exception.NoResultsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
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
    @Value("${title.actors.boost}")
    private float titleActorsBoost;
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

    public SearchResultDto search(String query, int page, int size, boolean fuzzySearch, boolean orQuery) throws InternalServerErrorException, NoResultsException {
        try {
            QueryBuilder queryBuilder = createQuery(query, fuzzySearch, orQuery);
            SearchRequest searchRequest = createSearchRequest(queryBuilder, page, size);
            SearchResponse searchResponse = elasticsearchService.executeQuery(searchRequest);
            SearchResultDto searchResultDto = conversionService.convert(searchResponse, SearchResultDto.class);
            if (searchResultDto.getResults().size() == 0) {
                throw new NoResultsException();
            }
            return searchResultDto;
        } catch (NoResultsException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.catching(e);
            throw new InternalServerErrorException();
        }
    }

    /**
     * creates elasticsearch query by user query.
     *
     * @param query
     * @param fuzzySearch
     * @param orQuery
     * @return
     */
    private QueryBuilder createQuery(String query, boolean fuzzySearch, boolean orQuery) {
        DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();
        if (!orQuery) {
            disMaxQueryBuilder.add(QueryBuilders.matchPhraseQuery(ElasticsearchFields.TITLE.getFieldName(), query).boost(titleBoost).slop(searchSlop));
            disMaxQueryBuilder.add(QueryBuilders.matchPhraseQuery(ElasticsearchFields.PARSED_ACTORS.getFieldName(), query).boost(actorsBoost).slop(searchSlop).boost(titleBoost).slop(searchSlop));
            disMaxQueryBuilder.add(QueryBuilders.matchPhraseQuery(ElasticsearchFields.TITLE_ACTORS_SEARCH.getFieldName(), query).boost(titleActorsBoost).slop(searchSlop).boost(titleBoost).slop(searchSlop));
        } else {
            disMaxQueryBuilder.add(QueryBuilders.matchQuery(ElasticsearchFields.TITLE.getFieldName(), query).boost(titleBoost).operator(Operator.OR).fuzziness(Fuzziness.ZERO).minimumShouldMatch(minimumShouldMatch));
            disMaxQueryBuilder.add(QueryBuilders.matchQuery(ElasticsearchFields.PARSED_ACTORS.getFieldName(), query).boost(actorsBoost).operator(Operator.OR).fuzziness(Fuzziness.ZERO).minimumShouldMatch(minimumShouldMatch));
            disMaxQueryBuilder.add(QueryBuilders.matchQuery(ElasticsearchFields.TITLE_ACTORS_SEARCH.getFieldName(), query).boost(titleActorsBoost).operator(Operator.OR).fuzziness(Fuzziness.ZERO).minimumShouldMatch(minimumShouldMatch));
        }
        if (fuzzySearch) {
            disMaxQueryBuilder.add(QueryBuilders.matchQuery(ElasticsearchFields.TITLE.getFieldName(), query).boost(titleBoost * fuzzyQueryBoost).operator(Operator.AND).fuzziness(Fuzziness.build(fuzziness)));
            disMaxQueryBuilder.add(QueryBuilders.matchQuery(ElasticsearchFields.PARSED_ACTORS.getFieldName(), query).boost(actorsBoost * fuzzyQueryBoost).operator(Operator.AND).fuzziness(Fuzziness.build(fuzziness)));
            disMaxQueryBuilder.add(QueryBuilders.matchQuery(ElasticsearchFields.TITLE_ACTORS_SEARCH.getFieldName(), query).boost(titleActorsBoost * fuzzyQueryBoost).operator(Operator.AND).fuzziness(Fuzziness.build(fuzziness)));
        }
        return disMaxQueryBuilder;
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

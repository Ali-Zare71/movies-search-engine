package de.kaufland.moviesearch.indexservice.searchservice.controller;

import de.kaufland.moviesearch.indexservice.searchservice.model.dto.MovieDto;
import de.kaufland.moviesearch.indexservice.searchservice.model.dto.SearchResultDto;
import de.kaufland.moviesearch.indexservice.searchservice.model.exception.InternalServerErrorException;
import de.kaufland.moviesearch.indexservice.searchservice.model.exception.NoResultsException;
import de.kaufland.moviesearch.indexservice.searchservice.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@RestController
@Api(tags = "Search API", value = " ")
@RequestMapping("/api/movies")
@Validated
public class MovieController {
    private final SearchService searchService;

    public MovieController(SearchService searchService) {
        this.searchService = searchService;
    }


    @ApiOperation(value = "A list of available movies that are related to given query", response = MovieDto.class, responseContainer = "SearchResultDto")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public SearchResultDto<MovieDto> searchQuery(
            @ApiParam(value = "User query")
            @Size(min = 1, max = 255, message = "query length must be in 1 - 255 characters")
            @RequestParam(value = "query") String query,
            @ApiParam(value = "Results page number (starts from 1)")
            @Min(value = 1, message = "minimum page number must be 1")
            @Max(value = 100, message = "maximum page number must be 100")
            @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "Size of each page (maximum is 100)")
            @Min(value = 1, message = "minimum size of songs must be 1")
            @Max(value = 100, message = "maximum size of songs must be 100")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @ApiParam(value = "turn fuzzy search on or off")
            @RequestParam(value = "fuzzy-search", defaultValue = "true") boolean fuzzySearch,
            @ApiParam(value = "enable or query to retrieve more documents")
            @RequestParam(value = "or-query", defaultValue = "true") boolean orQuery

    ) throws InternalServerErrorException, NoResultsException {
        return searchService.search(query, page, size, fuzzySearch, orQuery);
    }
}

package de.kaufland.moviesearch.indexservice.searchservice.controller;

import de.kaufland.moviesearch.indexservice.searchservice.model.dto.MovieDto;
import de.kaufland.moviesearch.indexservice.searchservice.model.dto.SearchResultDto;
import de.kaufland.moviesearch.indexservice.searchservice.service.SearchService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Search API", value = " ")
@RequestMapping("/movies")
public class MovieController {
    private final SearchService searchService;

    public MovieController(SearchService searchService) {
        this.searchService = searchService;
    }


    @ApiOperation(value = "A list of available movies that are related to given query", response = MovieDto.class, responseContainer = "SearchResultDto")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully returned the result."),
            @ApiResponse(code = 500, message = "Internal server error.")
    })
    @RequestMapping(value = "", method = RequestMethod.GET)
    public SearchResultDto<MovieDto> searchQuery(
            @ApiParam(value = "User query")
            @RequestParam(value = "query") String query,
            @ApiParam(value = "Results page number (starts from 1)")
            @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "Size of each page (maximum is 100)")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @ApiParam(value = "turn fuzzy search on or off")
            @RequestParam(value = "fuzzy-search", defaultValue = "true") boolean fuzzySearch,
            @ApiParam(value = "enable or query to retrieve more documents")
            @RequestParam(value = "or-query", defaultValue = "true") boolean orQuery

    ) {
        return searchService.search(query, page, size, fuzzySearch, orQuery);
    }
}

package de.kaufland.moviesearch.indexservice.searchservice.controller;

import de.kaufland.moviesearch.indexservice.searchservice.model.dto.SearchResultsDto;
import de.kaufland.moviesearch.indexservice.searchservice.service.SearchService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Search API", value = " ")
@RequestMapping("/movie")
public class MovieController {
    private final SearchService searchService;

    public MovieController(SearchService searchService) {
        this.searchService = searchService;
    }


    @ApiOperation(value = "A list of available songs that are related to given query", response = SearchResultsDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully returned the result."),
            @ApiResponse(code = 500, message = "Internal server error.")
    })
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public SearchResultsDto searchQuery(
            @ApiParam(value = "User query")
            @RequestParam(value = "query") String query,
            @ApiParam(value = "Results page number (starts from 1)")
            @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "Size of each page (maximum is 100)")
            @RequestParam(value = "size", defaultValue = "10") int size

    ) {
        return searchService.search(query, page, size);
    }
}

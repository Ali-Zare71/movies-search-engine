package de.kaufland.moviesearch.ingestionapiservice.controller;

import de.kaufland.moviesearch.ingestionapiservice.model.rabbitmq.MovieModel;
import de.kaufland.moviesearch.ingestionapiservice.service.StoreMoviesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Api(tags = "Movies input api", value = " ")
@RequestMapping("/movies")
public class MoviesController {
    private final StoreMoviesService storeMoviesService;

    public MoviesController(StoreMoviesService storeMoviesService) {
        this.storeMoviesService = storeMoviesService;
    }

    @ApiOperation(value = "uploads data by uploading a json file")
    @RequestMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    public ResponseEntity<Void> uploadFile(@RequestPart MultipartFile file) {
        storeMoviesService.store(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "uploads json data")
    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addEvents(@RequestBody List<MovieModel> eventRequests
    ) {
        storeMoviesService.store(eventRequests);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

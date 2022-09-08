package de.kaufland.moviesearch.indexservice.model.index;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndexMovieModel {
    private long id;
    private String title;
    private String year;
    private String runtime;
    private List<String> genres;
    private String director;
    private List<String> parsedActors;
    private String actors;
    private String plot;
    private String posterUrl;
}

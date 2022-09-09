package de.kaufland.moviesearch.indexservice.searchservice.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieDto {
    private long id;
    private String title;
    private String year;
    private String runtime;
    private List<String> genres;
    private String director;
    private String actors;
    private String plot;
    private String posterUrl;
}

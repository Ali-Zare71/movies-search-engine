package de.kaufland.moviesearch.indexservice.model.rabbitmq;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MovieModel implements Serializable {
    private Long id;
    private String title;
    private String year;
    private String runtime;
    private List<String> genres;
    private String director;
    private String actors;
    private String plot;
    private String posterUrl;

}

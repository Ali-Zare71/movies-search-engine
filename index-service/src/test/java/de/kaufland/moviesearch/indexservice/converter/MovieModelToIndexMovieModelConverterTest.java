package de.kaufland.moviesearch.indexservice.converter;

import de.kaufland.moviesearch.indexservice.model.index.IndexMovieModel;
import de.kaufland.moviesearch.indexservice.model.rabbitmq.MovieModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

class MovieModelToIndexMovieModelConverterTest {
    MovieModelToIndexMovieModelConverter converter = new MovieModelToIndexMovieModelConverter();
    MovieModel movieModel1;
    IndexMovieModel indexMovieModel1;

    MovieModel movieModel2;
    IndexMovieModel indexMovieModel2;

    public MovieModelToIndexMovieModelConverterTest() {
        movieModel1 = new MovieModel(1l, "Beetlejuice", "1988", "92", new ArrayList<>(),
                "Tim Burton", "Alec Baldwin, Geena Davis", "plot", "https://a.com");
        movieModel1.getGenres().add("Comedy");
        indexMovieModel1 = new IndexMovieModel(1l, "Beetlejuice", "1988", "92", new ArrayList<>(),
                "Tim Burton", new ArrayList<>(), "Alec Baldwin, Geena Davis"
                , "plot", "https://a.com");
        indexMovieModel1.getGenres().add("Comedy");
        indexMovieModel1.getParsedActors().add("Alec Baldwin");
        indexMovieModel1.getParsedActors().add("Geena Davis");


        movieModel2 = new MovieModel(1l, "Beetlejuice", "1988", "92", new ArrayList<>(),
                "Tim Burton", "Alec Baldwin", "plot", "https://a.com");
        movieModel2.getGenres().add("Comedy");
        indexMovieModel2 = new IndexMovieModel(1l, "Beetlejuice", "1988", "92", new ArrayList<>(),
                "Tim Burton", new ArrayList<>(), "Alec Baldwin", "plot", "https://a.com");
        indexMovieModel2.getGenres().add("Comedy");
        indexMovieModel2.getParsedActors().add("Alec Baldwin");
    }

    @Test
    public void testConvert() {
        assertEquals(converter.convert(movieModel1).toString(), indexMovieModel1.toString());
        assertEquals(converter.convert(movieModel2).toString(), indexMovieModel2.toString());
        assertNull(converter.convert(null));
    }

}

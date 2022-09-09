package de.kaufland.moviesearch.ingestionapiservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kaufland.moviesearch.ingestionapiservice.model.exception.FileIsNotParsableException;
import de.kaufland.moviesearch.ingestionapiservice.model.exception.InternalServerErrorException;
import de.kaufland.moviesearch.ingestionapiservice.model.exception.InvalidIdException;
import de.kaufland.moviesearch.ingestionapiservice.model.rabbitmq.MovieModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class StoreMoviesService {
    private static final Logger LOGGER = LogManager.getLogger(StoreMoviesService.class.getName());
    private final ObjectMapper objectMapper;
    private final RabbitMqProducerService rabbitMqProducerService;

    public StoreMoviesService(ObjectMapper objectMapper, RabbitMqProducerService rabbitMqProducerService) {
        this.objectMapper = objectMapper;
        this.rabbitMqProducerService = rabbitMqProducerService;
    }

    /**
     * parse file to movie models and store them in rabbitmq
     *
     * @param file
     */
    public void store(MultipartFile file) throws FileIsNotParsableException, InvalidIdException, InternalServerErrorException {
        List<MovieModel> movieModels = new ArrayList<>();
        try {
            movieModels = objectMapper.readValue(new String(file.getBytes()), new TypeReference<List<MovieModel>>() {
            });
        } catch (Exception e) {
            LOGGER.catching(e);
            throw new FileIsNotParsableException();
        }
        store(movieModels);
    }

    /**
     * store models in rabbitmq
     *
     * @param movieModels
     */
    public void store(List<MovieModel> movieModels) throws InvalidIdException, InternalServerErrorException {
        validateData(movieModels);
        for (MovieModel movieModel : movieModels) {
            rabbitMqProducerService.send(movieModel);
        }
    }

    private void validateData(List<MovieModel> movieModels) throws InvalidIdException {
        for (MovieModel movieModel : movieModels) {
            if (movieModel.getId() == null) {
                throw new InvalidIdException();
            }
        }
    }

}

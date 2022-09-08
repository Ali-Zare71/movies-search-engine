package de.kaufland.moviesearch.ingestionapiservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kaufland.moviesearch.ingestionapiservice.model.rabbitmq.MovieModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public void store(MultipartFile file) {
        try {
            List<MovieModel> movieModels = objectMapper.readValue(new String(file.getBytes()), new TypeReference<List<MovieModel>>() {
            });
            store(movieModels);
        } catch (Exception e) {
            LOGGER.catching(e);
        }
    }

    /**
     * store models in rabbitmq
     *
     * @param movieModels
     */
    public void store(List<MovieModel> movieModels) {
        for (MovieModel movieModel : movieModels) {
            rabbitMqProducerService.send(movieModel);
        }
    }

}

package de.kaufland.moviesearch.ingestionapiservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kaufland.moviesearch.ingestionapiservice.model.exception.InternalServerErrorException;
import de.kaufland.moviesearch.ingestionapiservice.model.rabbitmq.MovieModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqProducerService {
    private static final Logger infoLog = LogManager.getLogger("infoLog");
    private static final Logger LOGGER = LogManager.getLogger(RabbitMqProducerService.class.getName());
    private final RabbitTemplate template;
    private final Queue queue;
    private final ObjectMapper objectMapper;


    public RabbitMqProducerService(RabbitTemplate template, Queue queue, ObjectMapper objectMapper) {
        this.template = template;
        this.queue = queue;
        this.objectMapper = objectMapper;
    }

    /**
     * stores a movie to rabbitmq
     *
     * @param movieModel
     */
    public void send(MovieModel movieModel) throws InternalServerErrorException {
        try {
            String message = objectMapper.writeValueAsString(movieModel);
            this.template.convertAndSend(queue.getName(), message);
        } catch (Exception e) {
            LOGGER.catching(e);
            throw new InternalServerErrorException();
        }
    }
}

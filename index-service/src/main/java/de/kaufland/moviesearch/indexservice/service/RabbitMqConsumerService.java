package de.kaufland.moviesearch.indexservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kaufland.moviesearch.indexservice.model.rabbitmq.MovieModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
@RabbitListener(queues = "#{queue.getName()}")
public class RabbitMqConsumerService {
    private static final Logger infoLog = LogManager.getLogger("infoLog");
    private static final Logger LOGGER = LogManager.getLogger(RabbitMqConsumerService.class.getName());
    private final Queue queue;
    private final ObjectMapper objectMapper;

    public RabbitMqConsumerService(Queue queue, ObjectMapper objectMapper) {
        this.queue = queue;
        this.objectMapper = objectMapper;
    }

    @RabbitHandler
    public void receive(String in) {
        try {
            MovieModel movieRabbitModel = objectMapper.readValue(in, MovieModel.class);
            System.out.println();
            //// TODO: 9/8/2022 store document to elastic
        } catch (Exception e) {
            LOGGER.catching(e);
        }
    }
}

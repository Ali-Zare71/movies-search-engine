package de.kaufland.moviesearch.ingestionapiservice.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Can not store results to RabbitMQ")
public class InternalServerErrorException extends Exception {
}
package de.kaufland.moviesearch.ingestionapiservice.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "All of records must have id field.")
public class InvalidIdException extends Exception {
}
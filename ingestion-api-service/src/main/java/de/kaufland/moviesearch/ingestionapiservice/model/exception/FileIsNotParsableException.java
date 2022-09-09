package de.kaufland.moviesearch.ingestionapiservice.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Can not parse the file.")
public class FileIsNotParsableException extends Exception {
}
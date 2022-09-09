package de.kaufland.moviesearch.indexservice.searchservice.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No result matched the given query.")
public class NoResultsException extends Exception {
}
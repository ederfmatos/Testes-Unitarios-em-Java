package com.ederfmatos.testesunitarios.exceptions;

public class FilmeSemEstoqueException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FilmeSemEstoqueException(String message) {
		super(message);
	}

}

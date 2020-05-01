package com.ederfmatos.testesunitarios.builders;

import com.ederfmatos.testesunitarios.entidades.Filme;

public class FilmeBuilder {

	private FilmeBuilder() {
	}

	private static Filme filme;

	private static FilmeBuilder setDefaultUser() {
		filme = new Filme("Nome do filme", 10, 50.0);
		return new FilmeBuilder();
	}

	public static FilmeBuilder umFilme() {
		return setDefaultUser();
	}

	public Filme agora() {
		return filme;
	}

}

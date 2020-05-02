package com.ederfmatos.testesunitarios.builders;

import java.util.Arrays;
import java.util.List;

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
	
	public FilmeBuilder semEstoque() {
		filme.setEstoque(0);
		return this;
	}
	
	public FilmeBuilder comPreco(Double preco) {
		filme.setPrecoLocacao(preco);
		return this;
	}
	
	public FilmeBuilder comEstoque(int estoque) {
		filme.setEstoque(estoque);
		return this;
	}

	public Filme agora() {
		return filme;
	}
	
	public List<Filme> numaLista() {
		return Arrays.asList(filme);
	}

}

package com.ederfmatos.testesunitarios.builders;

import static com.ederfmatos.testesunitarios.builders.FilmeBuilder.umFilme;
import static com.ederfmatos.testesunitarios.builders.UsuarioBuilder.umUsuario;
import static com.ederfmatos.testesunitarios.utils.DataUtils.obterDataComDiferencaDias;

import java.util.Arrays;
import java.util.Date;

import com.ederfmatos.testesunitarios.entidades.Locacao;
import com.ederfmatos.testesunitarios.entidades.Usuario;

public class LocacaoBuilder {

	private LocacaoBuilder() {
	}

	private static Locacao locacao;

	private static LocacaoBuilder setDefaultParams() {
		locacao = new Locacao();
		locacao.setValor(10.0);
		locacao.setDataLocacao(new Date());
		locacao.setDataRetorno(obterDataComDiferencaDias(1));
		locacao.setUsuario(umUsuario().agora());
		locacao.setFilmes(Arrays.asList(umFilme().agora()));

		return new LocacaoBuilder();
	}

	public static LocacaoBuilder umaLocacao() {
		return setDefaultParams();
	}

	public LocacaoBuilder comUsuario(Usuario usuario) {
		locacao.setUsuario(usuario);
		return this;
	}

	public LocacaoBuilder comDataRetorno(Date date) {
		locacao.setDataRetorno(date);
		return this;
	}

	public Locacao agora() {
		return locacao;
	}

}

package com.ederfmatos.testesunitarios.servicos;

import static com.ederfmatos.testesunitarios.utils.DataUtils.adicionarDias;
import static com.ederfmatos.testesunitarios.utils.DataUtils.obterDataComDiferencaDias;
import static com.ederfmatos.testesunitarios.utils.DataUtils.verificarDiaSemana;
import static java.util.Calendar.SUNDAY;

import java.util.Date;
import java.util.List;

import com.ederfmatos.testesunitarios.daos.LocacaoDAO;
import com.ederfmatos.testesunitarios.entidades.Filme;
import com.ederfmatos.testesunitarios.entidades.Locacao;
import com.ederfmatos.testesunitarios.entidades.Usuario;
import com.ederfmatos.testesunitarios.exceptions.FilmeSemEstoqueException;
import com.ederfmatos.testesunitarios.exceptions.LocadoraException;
import com.ederfmatos.testesunitarios.exceptions.NegativacaoSpcException;
import com.ederfmatos.testesunitarios.exceptions.SPCException;

public class LocacaoService {

	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes)
			throws LocadoraException, FilmeSemEstoqueException, NegativacaoSpcException, SPCException {
		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Ao menos um filme é obrigatório");
		}

		for (Filme filme : filmes) {
			if (filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException("Filme sem estoque");
			}
		}

		if (usuario == null) {
			throw new LocadoraException("Usuário é obrigatório");
		}

		try {
			if (spcService.possuiNegativacao(usuario)) {
				throw new NegativacaoSpcException("Usuário negativado");
			}
		} catch (SPCException e) {
			throw new LocadoraException("Problemas no SPC, tente novamente");
		}

		calcularDescontoFilmes(filmes);

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(obterDataAtual());
		locacao.setValor(calcularValorLocacao(filmes));

		Date dataEntrega = obterDataAtual();
		dataEntrega = adicionarDias(dataEntrega, 1);

		if (verificarDiaSemana(dataEntrega, SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}

		locacao.setDataRetorno(dataEntrega);

		dao.save(locacao);

		return locacao;
	}

	protected Date obterDataAtual() {
		return new Date();
	}

	protected double calcularValorLocacao(List<Filme> filmes) {
		return filmes.stream().mapToDouble(filme -> filme.getPrecoLocacao()).reduce((total, filme) -> total + filme)
				.orElse(0);
	}

	protected void calcularDescontoFilmes(List<Filme> filmes) {
		for (Filme filme : filmes) {
			int index = filmes.indexOf(filme);

			Double precoLocacao = filme.getPrecoLocacao();

			switch (index) {
			case 2:
				precoLocacao = precoLocacao * 0.75;
				break;
			case 3:
				precoLocacao = precoLocacao * 0.50;
				break;
			case 4:
				precoLocacao = precoLocacao * 0.25;
				break;
			case 5:
				precoLocacao = 0.0;
			}

			filme.setPrecoLocacao(precoLocacao);
		}
	}

	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();

		for (Locacao locacao : locacoes) {
			emailService.notificarAtraso(locacao);
		}
	}

	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setDataLocacao(obterDataAtual());
		novaLocacao.setDataRetorno(obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);

		dao.save(novaLocacao);
	}

}
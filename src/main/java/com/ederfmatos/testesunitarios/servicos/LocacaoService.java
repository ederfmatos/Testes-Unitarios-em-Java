package com.ederfmatos.testesunitarios.servicos;

import static com.ederfmatos.testesunitarios.utils.DataUtils.adicionarDias;
import static com.ederfmatos.testesunitarios.utils.DataUtils.verificarDiaSemana;
import static java.util.Calendar.SUNDAY;

import java.util.Date;
import java.util.List;

import com.ederfmatos.testesunitarios.daos.LocacaoDAO;
import com.ederfmatos.testesunitarios.entidades.Filme;
import com.ederfmatos.testesunitarios.entidades.Locacao;
import com.ederfmatos.testesunitarios.entidades.Usuario;
import com.ederfmatos.testesunitarios.exceptions.FilmeSemEstoqueException;
import com.ederfmatos.testesunitarios.exceptions.NegativacaoSpcException;

public class LocacaoService {

	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws Exception {
		if (filmes == null || filmes.isEmpty()) {
			throw new Exception("Ao menos um filme é obrigatório");
		}

		for (Filme filme : filmes) {
			if (filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException("Filme sem estoque");
			}
		}

		if (usuario == null) {
			throw new Exception("Usuário é obrigatório");
		}

		if (spcService.possuiNegativacao(usuario)) {
			throw new NegativacaoSpcException("Usuário negativado");
		}

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

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(filmes.stream().mapToDouble(filme -> filme.getPrecoLocacao())
				.reduce((total, filme) -> total + filme).orElse(0));

		Date dataEntrega = new Date();

		dataEntrega = adicionarDias(dataEntrega, 1);

		if (verificarDiaSemana(dataEntrega, SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}

		locacao.setDataRetorno(dataEntrega);

		dao.save(locacao);

		return locacao;
	}

	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();

		for (Locacao locacao : locacoes) {
			emailService.notificarAtraso(locacao);
		}
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void setLocacaoDao(LocacaoDAO locacaoDAO) {
		this.dao = locacaoDAO;
	}

	public void setSPCService(SPCService spcService) {
		this.spcService = spcService;
	}

}
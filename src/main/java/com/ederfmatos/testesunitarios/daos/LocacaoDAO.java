package com.ederfmatos.testesunitarios.daos;

import java.util.List;

import com.ederfmatos.testesunitarios.entidades.Locacao;

public interface LocacaoDAO {
	
	public void save(Locacao locacao);

	public List<Locacao> obterLocacoesPendentes();
	
}

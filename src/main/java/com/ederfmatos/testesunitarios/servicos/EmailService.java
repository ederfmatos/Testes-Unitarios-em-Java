package com.ederfmatos.testesunitarios.servicos;

import com.ederfmatos.testesunitarios.entidades.Locacao;

public interface EmailService {

	public void notificarAtraso(Locacao locacao);

}

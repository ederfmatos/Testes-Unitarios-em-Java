package com.ederfmatos.testesunitarios.servicos;

import com.ederfmatos.testesunitarios.entidades.Usuario;
import com.ederfmatos.testesunitarios.exceptions.SPCException;

public interface SPCService {

	public boolean possuiNegativacao(Usuario usuario) throws SPCException;

}

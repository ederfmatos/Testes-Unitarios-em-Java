package com.ederfmatos.testesunitarios.builders;

import com.ederfmatos.testesunitarios.entidades.Usuario;

public class UsuarioBuilder {

	private UsuarioBuilder() {
	}

	private static Usuario usuario;

	private static UsuarioBuilder setDefaultUser() {
		usuario = new Usuario("Nome do usuário");
		return new UsuarioBuilder();
	}

	public static UsuarioBuilder umUsuario() {
		return setDefaultUser();
	}
	
	public UsuarioBuilder comNome(String nome) {
		usuario.setNome(nome);
		return this;
	}

	public Usuario agora() {
		return usuario;
	}

}

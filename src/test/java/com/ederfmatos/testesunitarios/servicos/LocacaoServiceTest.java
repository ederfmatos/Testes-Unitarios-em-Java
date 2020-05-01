package com.ederfmatos.testesunitarios.servicos;

import static com.ederfmatos.testesunitarios.matchers.PersonalMatchers.caiEm;
import static com.ederfmatos.testesunitarios.matchers.PersonalMatchers.ehAmanha;
import static com.ederfmatos.testesunitarios.matchers.PersonalMatchers.ehHoje;
import static com.ederfmatos.testesunitarios.utils.DataUtils.verificarDiaSemana;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.mockito.Mockito;

import com.ederfmatos.testesunitarios.daos.LocacaoDAO;
import com.ederfmatos.testesunitarios.entidades.Filme;
import com.ederfmatos.testesunitarios.entidades.Locacao;
import com.ederfmatos.testesunitarios.entidades.Usuario;
import com.ederfmatos.testesunitarios.exceptions.FilmeSemEstoqueException;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();

	private LocacaoService service;
	private LocacaoDAO locacaoDAO;

	@Before
	public void beforeTest() {
		service = new LocacaoService();
		locacaoDAO = Mockito.mock(LocacaoDAO.class);
		service.setLocacaoDao(locacaoDAO);
	}

	@After
	public void afterTest() {
	}

	@BeforeClass
	public static void beforeClass() {
	}

	@AfterClass
	public static void afterClass() {
	}

	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(verificarDiaSemana(new Date(), SATURDAY));

		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("VEF", 2, 25.0);

		Locacao locacao = service.alugarFilme(usuario, Arrays.asList(filme));

		error.checkThat(locacao.getValor(), is(equalTo(25.00)));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		error.checkThat(locacao.getDataRetorno(), ehAmanha());
	}

	@Test(expected = Exception.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("VEF", 0, 25.0);

		service.alugarFilme(usuario, Arrays.asList(filme));
	}

	@Test
	public void testeLocacaoSemEstoqueComTryCatch() {
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("VEF", 0, 25.0);

		try {
			service.alugarFilme(usuario, Arrays.asList(filme));
			fail("Deveria ter lançado exceção");
		} catch (Exception e) {
			error.checkThat(e.getMessage(), is(equalTo("Filme sem estoque")));
		}
	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void testeLocacaoSemEstoque3() throws Exception {
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("VEF", 0, 25.0);

		service.alugarFilme(usuario, Arrays.asList(filme));
	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() {
		Usuario usuario = new Usuario("Usuario 1");

		try {
			service.alugarFilme(usuario, null);
			fail("Deveria ter lançado exceção");
		} catch (Exception e) {
			error.checkThat(e.getMessage(), is(equalTo("Ao menos um filme é obrigatório")));
		}
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() {
		Filme filme = new Filme("VEF", 5, 25.0);

		try {
			service.alugarFilme(null, Arrays.asList(filme));
			fail("Deveria ter lançado exceção");
		} catch (Exception e) {
			error.checkThat(e.getMessage(), is(equalTo("Usuário é obrigatório")));
		}
	}

	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		Assume.assumeTrue(verificarDiaSemana(new Date(), SATURDAY));

		Usuario usuario = new Usuario("usuario");

		List<Filme> filmes = Arrays.asList(new Filme("Velozes e furiosos 1", 5, 10.0));

		Locacao locacao = service.alugarFilme(usuario, filmes);

		error.checkThat(locacao.getDataRetorno(), caiEm(MONDAY));
	}

}

package com.ederfmatos.testesunitarios.servicos;

import static com.ederfmatos.testesunitarios.utils.DataUtils.isMesmaData;
import static com.ederfmatos.testesunitarios.utils.DataUtils.obterDataComDiferencaDias;
import static com.ederfmatos.testesunitarios.utils.DataUtils.verificarDiaSemana;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
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

import com.ederfmatos.testesunitarios.entidades.Filme;
import com.ederfmatos.testesunitarios.entidades.Locacao;
import com.ederfmatos.testesunitarios.entidades.Usuario;
import com.ederfmatos.testesunitarios.exceptions.FilmeSemEstoqueException;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();

	private LocacaoService service;

	@Before
	public void beforeTest() {
		service = new LocacaoService();
//		System.out.println("Antes de cada método");
	}

	@After
	public void afterTest() {
//		System.out.println("Após cada método");
	}

	@BeforeClass
	public static void beforeClass() {
//		System.out.println("Antes da classe ser instanciada, deve ser estático");
	}

	@AfterClass
	public static void afterClass() {
//		System.out.println("Depois da classe ser instanciada, deve ser estático");
	}

	@Test
	public void deveAlugarFilme() throws Exception {
		// Cenário

		Assume.assumeFalse(verificarDiaSemana(new Date(), SATURDAY));

		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("VEF", 2, 25.0);

		// Ação
		Locacao locacao = service.alugarFilme(usuario, Arrays.asList(filme));

		// Verificação
		error.checkThat(locacao.getValor(), is(equalTo(25.00)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
	}

	@Test(expected = Exception.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		// Cenário
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("VEF", 0, 25.0);

		// Ação
		service.alugarFilme(usuario, Arrays.asList(filme));
	}

	@Test
	public void testeLocacaoSemEstoqueComTryCatch() {
		// Cenário
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("VEF", 0, 25.0);

		try {
			// Ação
			service.alugarFilme(usuario, Arrays.asList(filme));
			fail("Deveria ter lançado exceção");
		} catch (Exception e) {
			error.checkThat(e.getMessage(), is(equalTo("Filme sem estoque")));
		}
	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void testeLocacaoSemEstoque3() throws Exception {
		// Cenário
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("VEF", 0, 25.0);

		service.alugarFilme(usuario, Arrays.asList(filme));
	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() {
		// Cenário
		Usuario usuario = new Usuario("Usuario 1");

		try {
			// Ação
			service.alugarFilme(usuario, null);
			fail("Deveria ter lançado exceção");
		} catch (Exception e) {
			error.checkThat(e.getMessage(), is(equalTo("Ao menos um filme é obrigatório")));
		}
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() {
		// Cenário
		Filme filme = new Filme("VEF", 5, 25.0);

		try {
			// Ação
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

		boolean isMonday = verificarDiaSemana(locacao.getDataRetorno(), MONDAY);

		assertTrue(isMonday);
	}

}

package com.ederfmatos.testesunitarios.servicos;

import static com.ederfmatos.testesunitarios.utils.DataUtils.isMesmaData;
import static com.ederfmatos.testesunitarios.utils.DataUtils.obterDataComDiferencaDias;
import static com.ederfmatos.testesunitarios.utils.DataUtils.verificarDiaSemana;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
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
	public void devePagar25PorcentoNoFilme3() throws Exception {
		Usuario usuario = new Usuario("usuario");

		List<Filme> filmes = Arrays.asList(new Filme("Velozes e furiosos 1", 5, 10.0),
				new Filme("Velozes e furiosos 1", 5, 10.0), new Filme("Velozes e furiosos 1", 5, 10.0));

		Locacao locacao = service.alugarFilme(usuario, filmes);

		error.checkThat("Terceiro filme deve ter 25% de desconto, logo seu valor final devera ser 7,50",
				locacao.getFilmes().get(2).getPrecoLocacao(), is(equalTo(7.5)));
	}

	@Test
	public void devePagar50PorcentoNoFilme4() throws Exception {
		Usuario usuario = new Usuario("usuario");

		List<Filme> filmes = Arrays.asList(new Filme("Velozes e furiosos 1", 5, 10.0),
				new Filme("Velozes e furiosos 2", 5, 10.0), new Filme("Velozes e furiosos 3", 5, 10.0),
				new Filme("Velozes e furiosos 4", 5, 10.0));

		Locacao locacao = service.alugarFilme(usuario, filmes);

		error.checkThat("Quarto filme deve ter 50% de desconto, logo seu valor final devera ser 5,00",
				locacao.getFilmes().get(3).getPrecoLocacao(), is(equalTo(5.0)));
	}

	@Test
	public void devePagar75PorcentoNoFilme5() throws Exception {
		Usuario usuario = new Usuario("usuario");

		List<Filme> filmes = Arrays.asList(new Filme("Velozes e furiosos 1", 5, 10.0),
				new Filme("Velozes e furiosos 2", 5, 10.0), new Filme("Velozes e furiosos 3", 5, 10.0),
				new Filme("Velozes e furiosos 4", 5, 10.0), new Filme("Velozes e furiosos 5", 5, 10.0));

		Locacao locacao = service.alugarFilme(usuario, filmes);

		error.checkThat("Quinto filme deve ter 75% de desconto, logo seu valor final devera ser 2,50",
				locacao.getFilmes().get(4).getPrecoLocacao(), is(equalTo(2.5)));
	}

	@Test
	public void devePagar100PorcentoNoFilme6() throws Exception {
		Usuario usuario = new Usuario("usuario");

		List<Filme> filmes = Arrays.asList(new Filme("Velozes e furiosos 1", 5, 10.0),
				new Filme("Velozes e furiosos 2", 5, 10.0), new Filme("Velozes e furiosos 3", 5, 10.0),
				new Filme("Velozes e furiosos 4", 5, 10.0), new Filme("Velozes e furiosos 5", 5, 10.0),
				new Filme("Velozes e furiosos 6", 5, 10.0));

		Locacao locacao = service.alugarFilme(usuario, filmes);

		error.checkThat("Sexto filme deve ter 100% de desconto, logo seu valor final devera ser 0,00",
				locacao.getFilmes().get(5).getPrecoLocacao(), is(equalTo(0.0)));
	}

	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		Usuario usuario = new Usuario("usuario");

		List<Filme> filmes = Arrays.asList(new Filme("Velozes e furiosos 1", 5, 10.0));

		Locacao locacao = service.alugarFilme(usuario, filmes);

		boolean isMonday = verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);

		assertTrue(isMonday);
	}

}

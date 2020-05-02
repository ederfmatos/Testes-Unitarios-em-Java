package com.ederfmatos.testesunitarios.servicos;

import static com.ederfmatos.testesunitarios.builders.FilmeBuilder.umFilme;
import static com.ederfmatos.testesunitarios.builders.LocacaoBuilder.umaLocacao;
import static com.ederfmatos.testesunitarios.builders.UsuarioBuilder.umUsuario;
import static com.ederfmatos.testesunitarios.matchers.PersonalMatchers.caiEm;
import static com.ederfmatos.testesunitarios.matchers.PersonalMatchers.ehAmanha;
import static com.ederfmatos.testesunitarios.matchers.PersonalMatchers.ehHoje;
import static com.ederfmatos.testesunitarios.matchers.PersonalMatchers.ehHojeMaisDias;
import static com.ederfmatos.testesunitarios.utils.DataUtils.verificarDiaSemana;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.ederfmatos.testesunitarios.daos.LocacaoDAO;
import com.ederfmatos.testesunitarios.entidades.Filme;
import com.ederfmatos.testesunitarios.entidades.Locacao;
import com.ederfmatos.testesunitarios.entidades.Usuario;
import com.ederfmatos.testesunitarios.exceptions.FilmeSemEstoqueException;
import com.ederfmatos.testesunitarios.exceptions.NegativacaoSpcException;
import com.ederfmatos.testesunitarios.exceptions.SPCException;
import com.ederfmatos.testesunitarios.utils.DataUtils;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@InjectMocks
	private LocacaoService service;

	@Mock
	private LocacaoDAO locacaoDAO;

	@Mock
	private SPCService spcService;

	@Mock
	private EmailService emailService;

	@Before
	public void beforeTest() {
		initMocks(this);
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
	public void deveAlugarFilme() {
		assumeFalse(verificarDiaSemana(new Date(), SATURDAY));

		Locacao locacao = service.alugarFilme(umUsuario().agora(), umFilme().comEstoque(2).comPreco(25.0).numaLista());

		error.checkThat(locacao.getValor(), is(equalTo(25.00)));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		error.checkThat(locacao.getDataRetorno(), ehAmanha());
	}

	@Test(expected = Exception.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		service.alugarFilme(umUsuario().agora(), umFilme().semEstoque().comPreco(25.0).numaLista());
	}

	@Test
	public void testeLocacaoSemEstoqueComTryCatch() {
		try {
			service.alugarFilme(umUsuario().agora(), umFilme().semEstoque().comPreco(25.0).numaLista());
			fail("Deveria ter lançado exceção");
		} catch (Exception e) {
			error.checkThat(e.getMessage(), is(equalTo("Filme sem estoque")));
		}
	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void testeLocacaoSemEstoque3() throws Exception {
		service.alugarFilme(umUsuario().agora(), umFilme().semEstoque().comPreco(25.0).numaLista());
	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() {
		try {
			service.alugarFilme(umUsuario().agora(), null);
			fail("Deveria ter lançado exceção");
		} catch (Exception e) {
			error.checkThat(e.getMessage(), is(equalTo("Ao menos um filme é obrigatório")));
		}
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() {
		try {
			service.alugarFilme(null, umFilme().comEstoque(5).comPreco(25.0).numaLista());
			fail("Deveria ter lançado exceção");
		} catch (Exception e) {
			error.checkThat(e.getMessage(), is(equalTo("Usuário é obrigatório")));
		}
	}

	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		assumeTrue(verificarDiaSemana(new Date(), SATURDAY));

		Locacao locacao = service.alugarFilme(umUsuario().agora(), umFilme().comEstoque(5).comPreco(10.0).numaLista());

		error.checkThat(locacao.getDataRetorno(), caiEm(MONDAY));
	}

	@Test(expected = NegativacaoSpcException.class)
	public void naoDeveAlugarFilmeParaUsuarioNegativadoNoSPC() throws Exception {
		Usuario usuario = umUsuario().agora();
		when(spcService.possuiNegativacao(umUsuario().agora())).thenReturn(true);

		service.alugarFilme(usuario, umFilme().numaLista());
	}

	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		Locacao locacao = umaLocacao().comDataRetorno(DataUtils.obterDataComDiferencaDias(-2)).agora();

		List<Locacao> locacoes = Arrays.asList(locacao);

		when(locacaoDAO.obterLocacoesPendentes()).thenReturn(locacoes);

		service.notificarAtrasos();

		verify(emailService).notificarAtraso(locacao);
	}

	@Test
	public void deveTratarErroNoSPC() {
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = umFilme().numaLista();

		when(spcService.possuiNegativacao(usuario)).thenThrow(new SPCException("Deu ruim"));

		try {
			service.alugarFilme(usuario, filmes);
			fail();
		} catch (Exception e) {
			error.checkThat(e.getMessage(), is(equalTo("Problemas no SPC, tente novamente")));
		}
	}

	@Test
	public void deveProrrogarUmaLocacao() {
		Locacao locacao = umaLocacao().agora();

		service.prorrogarLocacao(locacao, 3);

		ArgumentCaptor<Locacao> captor = ArgumentCaptor.forClass(Locacao.class);

		verify(locacaoDAO).save(captor.capture());

		Locacao locacaoRetornada = captor.getValue();

		error.checkThat(locacaoRetornada.getValor(), is(equalTo(30.0)));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeMaisDias(3));
	}

}

package com.ederfmatos.testesunitarios.servicos;

import static com.ederfmatos.testesunitarios.builders.FilmeBuilder.umFilme;
import static com.ederfmatos.testesunitarios.builders.LocacaoBuilder.umaLocacao;
import static com.ederfmatos.testesunitarios.builders.UsuarioBuilder.umUsuario;
import static com.ederfmatos.testesunitarios.matchers.PersonalMatchers.caiNumaSegunda;
import static com.ederfmatos.testesunitarios.matchers.PersonalMatchers.ehHoje;
import static com.ederfmatos.testesunitarios.matchers.PersonalMatchers.ehHojeMaisDias;
import static com.ederfmatos.testesunitarios.utils.DataUtils.isMesmaData;
import static com.ederfmatos.testesunitarios.utils.DataUtils.obterData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import com.ederfmatos.testesunitarios.daos.LocacaoDAO;
import com.ederfmatos.testesunitarios.entidades.Filme;
import com.ederfmatos.testesunitarios.entidades.Locacao;
import com.ederfmatos.testesunitarios.entidades.Usuario;
import com.ederfmatos.testesunitarios.exceptions.FilmeSemEstoqueException;
import com.ederfmatos.testesunitarios.exceptions.LocadoraException;
import com.ederfmatos.testesunitarios.exceptions.NegativacaoSpcException;
import com.ederfmatos.testesunitarios.exceptions.SPCException;
import com.ederfmatos.testesunitarios.runners.ParallelRunner;
import com.ederfmatos.testesunitarios.utils.DataUtils;

@RunWith(ParallelRunner.class)
public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@InjectMocks
	@Spy
	private LocacaoService service;

	@Mock
	private LocacaoDAO locacaoDAO;

	@Mock
	private SPCService spcService;

	@Mock
	private EmailService emailService;

	@Before
	public void beforeTest() {
		System.out.println("Iniciando Locacao...");
		initMocks(this);
	}

	@After
	public void afterTest() {
		System.out.println("Finalizando Locacao...");
	}

	@BeforeClass
	public static void beforeClass() {
	}

	@AfterClass
	public static void afterClass() {
	}

	@Test
	public void deveAlugarFilme() throws Exception {
		Mockito.doReturn(obterData(1, 5, 2020)).when(service).obterDataAtual();
		Locacao locacao = service.alugarFilme(umUsuario().agora(), umFilme().comEstoque(2).comPreco(25.0).numaLista());

		error.checkThat(locacao.getValor(), equalTo(25.00));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), obterData(1, 5, 2020)), equalTo(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterData(2, 5, 2020)), equalTo(true));
	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		service.alugarFilme(umUsuario().agora(), umFilme().semEstoque().comPreco(25.0).numaLista());
	}

	@Test
	public void testeLocacaoSemEstoqueComTryCatch() {
		try {
			service.alugarFilme(umUsuario().agora(), umFilme().semEstoque().comPreco(25.0).numaLista());
			fail("Deveria ter lançado exceção");
		} catch (FilmeSemEstoqueException e) {
			error.checkThat(e.getMessage(), equalTo("Filme sem estoque"));
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
		} catch (LocadoraException e) {
			error.checkThat(e.getMessage(), equalTo("Ao menos um filme é obrigatório"));
		}
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() {
		try {
			service.alugarFilme(null, umFilme().comEstoque(5).comPreco(25.0).numaLista());
			fail("Deveria ter lançado exceção");
		} catch (LocadoraException e) {
			error.checkThat(e.getMessage(), equalTo("Usuário é obrigatório"));
		}
	}

	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		Mockito.doReturn(obterData(23, 5, 2020)).when(service).obterDataAtual();

		Locacao locacao = service.alugarFilme(umUsuario().agora(), umFilme().comEstoque(5).comPreco(10.0).numaLista());

		error.checkThat(locacao.getDataRetorno(), caiNumaSegunda());
	}

	@Test(expected = NegativacaoSpcException.class)
	public void naoDeveAlugarFilmeParaUsuarioNegativadoNoSPC() throws Exception {
		Usuario usuario = umUsuario().agora();
//		when(spcService.possuiNegativacao(umUsuario().agora())).thenReturn(true);
		Mockito.doReturn(true).when(spcService).possuiNegativacao(umUsuario().agora());

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
			error.checkThat(e.getMessage(), equalTo("Problemas no SPC, tente novamente"));
		}
	}

	@Test
	public void deveProrrogarUmaLocacao() {
		Locacao locacao = umaLocacao().agora();

		service.prorrogarLocacao(locacao, 3);

		ArgumentCaptor<Locacao> captor = ArgumentCaptor.forClass(Locacao.class);

		verify(locacaoDAO).save(captor.capture());

		Locacao locacaoRetornada = captor.getValue();

		error.checkThat(locacaoRetornada.getValor(), equalTo(30.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeMaisDias(3));
	}

	@Test
	public void deveAlugarFilmeSemDesconto() throws Exception {
		List<Filme> filmes = Arrays.asList(umFilme().agora(), umFilme().agora(), umFilme().agora());

		Mockito.doReturn(1.0).when(service).calcularValorLocacao(filmes);
		Locacao locacao = service.alugarFilme(umUsuario().agora(), filmes);

		error.checkThat(locacao.getValor(), equalTo(1.0));
	}

	@Test
	public void deveCalcularValorLocacao() throws Exception {
		List<Filme> filmes = Arrays.asList(umFilme().agora(), umFilme().agora(), umFilme().agora());

		Class<LocacaoService> clazz = LocacaoService.class;

		Method calcularValorLocacao = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		calcularValorLocacao.setAccessible(true);

		Double valor = (Double) calcularValorLocacao.invoke(service, filmes);

		error.checkThat(valor, equalTo(150.0));
	}

}

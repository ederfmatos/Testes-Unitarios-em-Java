package com.ederfmatos.testesunitarios.servicos;

import static com.ederfmatos.testesunitarios.builders.FilmeBuilder.umFilme;
import static com.ederfmatos.testesunitarios.builders.UsuarioBuilder.umUsuario;
import static com.ederfmatos.testesunitarios.matchers.PersonalMatchers.caiNumaSegunda;
import static com.ederfmatos.testesunitarios.utils.DataUtils.isMesmaData;
import static com.ederfmatos.testesunitarios.utils.DataUtils.obterData;
import static java.util.Arrays.asList;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.invokeMethod;

import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;

import com.ederfmatos.testesunitarios.daos.LocacaoDAO;
import com.ederfmatos.testesunitarios.entidades.Filme;
import com.ederfmatos.testesunitarios.entidades.Locacao;
import com.ederfmatos.testesunitarios.entidades.Usuario;

public class LocacaoServiceTest_PowerMock {

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
		service = PowerMockito.spy(service);
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

	public void deveAlugarFilme() throws Exception {
		Calendar instance = getInstance();
		instance.set(DAY_OF_MONTH, 1);
		instance.set(MONTH, MAY);
		instance.set(YEAR, 2020);

		mockStatic(Calendar.class);
		when(getInstance()).thenReturn(instance);

		Locacao locacao = service.alugarFilme(umUsuario().agora(), umFilme().comEstoque(2).comPreco(25.0).numaLista());

		error.checkThat(locacao.getValor(), equalTo(25.00));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), obterData(1, 5, 2020)), equalTo(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterData(2, 5, 2020)), equalTo(true));
	}

	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		Calendar instance = getInstance();
		instance.set(DAY_OF_MONTH, 2);
		instance.set(MONTH, MAY);
		instance.set(YEAR, 2020);

		mockStatic(Calendar.class);
		when(getInstance()).thenReturn(instance);

		Locacao locacao = service.alugarFilme(umUsuario().agora(), umFilme().comEstoque(5).comPreco(10.0).numaLista());

		error.checkThat(locacao.getDataRetorno(), caiNumaSegunda());

		verifyStatic(Calendar.class, times(2));
		Calendar.getInstance();
	}

	public void deveAlugarFilmeSemDesconto() throws Exception {
		Usuario usuario = umUsuario().agora();

		List<Filme> filmes = asList(umFilme().agora(), umFilme().agora(), umFilme().agora());

		doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		Locacao locacao = service.alugarFilme(usuario, filmes);

		error.checkThat(locacao.getValor(), equalTo(1.0));

		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
	}

	@Test
	public void deveCalcularValorLocacao() throws Exception {
		List<Filme> filmes = asList(umFilme().agora(), umFilme().agora(), umFilme().agora());

		Double valor = (Double) invokeMethod(service, "calcularValorLocacao", filmes);
		error.checkThat(valor, equalTo(150.0));
	}

}

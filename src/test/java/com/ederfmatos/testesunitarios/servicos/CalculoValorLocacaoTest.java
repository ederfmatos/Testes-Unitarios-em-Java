package com.ederfmatos.testesunitarios.servicos;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.ederfmatos.testesunitarios.entidades.Filme;
import com.ederfmatos.testesunitarios.entidades.Locacao;
import com.ederfmatos.testesunitarios.entidades.Usuario;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

	@Parameter
	public List<Filme> filmes;

	@Parameter(value = 1)
	public double valorLocacao;
	
	@Parameter(value = 2)
	public String cenario;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	private LocacaoService service;

	@Before
	public void beforeTest() {
		service = new LocacaoService();
	}

	private static Object[] getParametro(final int quantidaDeFilmes, final double valorEsperadoNoUltimoFilme, double percentualDeDesconto) {
		List<Filme> filmes = new ArrayList<>();

		for (int i = 0; i < quantidaDeFilmes; i++) {
			filmes.add(new Filme("Filme número " + i, 2, 10.0));
		}

		return new Object[] { filmes, valorEsperadoNoUltimoFilme, quantidaDeFilmes + " Filmes: " + percentualDeDesconto+ "% de desconto" };
	}

	@Parameters(name = "{2}")
	public static Collection<Object[]> getParametros() {
		return Arrays.asList(new Object[][] {
			getParametro(2, 10, 0), 
			getParametro(3, 7.5, 25), 
			getParametro(4, 5.0, 50), 
			getParametro(5, 2.5, 75), 
			getParametro(6, 0.0, 100),
			getParametro(7, 10, 0), 
			});
	}

	@Test
	public void deveCalcularValordaLocacaoConsiderandoDescontos() throws Exception {
		Usuario usuario = new Usuario("usuario");

		Locacao locacao = service.alugarFilme(usuario, filmes);

		error.checkThat(locacao.getFilmes().get(filmes.size() - 1).getPrecoLocacao(), is(equalTo(valorLocacao)));
	}

}

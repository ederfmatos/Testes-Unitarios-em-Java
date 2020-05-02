package com.ederfmatos.testesunitarios.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//@RunWith(ParallelRunner.class)
public class CalculadoraTest {

	private Calculadora calculadora;

	@Before
	public void beforeTest() {
		calculadora = new Calculadora();
	}
	
	@After
	public void after() {
	}

	@Test
	public void deveSomarDoisValores() {
		int a = 1;
		int b = 3;

		int soma = calculadora.soma(a, b);

		assertEquals("Deve retornar 4", 4, soma);
	}

	@Test
	public void deveSubtrairDoisValores() {
		int a = 10;
		int b = 3;

		int subtracao = calculadora.subtrair(a, b);

		assertEquals("Deve retornar 7", 7, subtracao);
	}

	@Test
	public void deveDividirDoisValores() {
		int a = 10;
		int b = 2;

		int divisao = calculadora.dividir(a, b);

		assertEquals("Deve retornar 7", 5, divisao);
	}

	@Test(expected = IllegalArgumentException.class)
	public void naodeveDividirUmNumeroPorZero() {
		int a = 10;
		int b = 0;

		calculadora.dividir(a, b);
	}

}

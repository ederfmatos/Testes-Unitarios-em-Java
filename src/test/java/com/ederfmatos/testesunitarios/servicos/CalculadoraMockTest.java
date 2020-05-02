package com.ederfmatos.testesunitarios.servicos;

import static java.lang.System.out;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

public class CalculadoraMockTest {

	@Mock
	private Calculadora calculadoraMock;

	@Spy
	private Calculadora calculadoraSpy;

	@Before
	public void setup() {
		initMocks(this);
	}

	@Test
	public void deveMostrarDiferencaEntreMocksSpy() {
		when(calculadoraMock.soma(1, 3)).thenCallRealMethod();
		when(calculadoraSpy.soma(1, 5)).thenReturn(9);
		doReturn(5).when(calculadoraSpy).soma(1, 5);

		out.println("Mock: " + calculadoraMock.soma(1, 3));
		out.println("Spy: " + calculadoraSpy.soma(10, 5));
		out.println("Spy: " + calculadoraSpy.soma(1, 5));
	}

}

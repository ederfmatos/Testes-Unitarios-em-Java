package com.ederfmatos.testesunitarios.servicos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ederfmatos.testesunitarios.entidades.Usuario;

public class AssertTest {

	@Test
	public void test() {
		assertTrue("Verifica se é verdadeiro", true);
		assertFalse(false);
		
		assertEquals(1.0, 1.0, 0.1);
		assertEquals(1, 1);
		
		assertEquals("A", "A");
		assertNotEquals("A", "As");
		
		Usuario usuario1 = new Usuario("Nome");
		Usuario usuario2 = new Usuario("Nome");
		
		assertEquals(usuario1, usuario2);
		assertNotSame(usuario1, usuario2);
	}

}

package com.ederfmatos.testesunitarios.matchers;

import static com.ederfmatos.testesunitarios.utils.DataUtils.buscaNomeDoDiaDaSemana;
import static com.ederfmatos.testesunitarios.utils.DataUtils.verificarDiaSemana;

import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class DiaSemanaMatcher extends TypeSafeDiagnosingMatcher<Date> {

	private final int diaSemana;

	public DiaSemanaMatcher(int diaSemana) {
		this.diaSemana = diaSemana;
	}
	
	public void describeTo(Description description) {
		description.appendText(buscaNomeDoDiaDaSemana(diaSemana));
	}

	@Override
	protected boolean matchesSafely(Date date, Description mismatchDescription) {
		return verificarDiaSemana(date, diaSemana);
	}

}

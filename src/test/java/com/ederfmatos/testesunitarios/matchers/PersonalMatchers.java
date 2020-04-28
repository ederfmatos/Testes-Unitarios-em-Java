package com.ederfmatos.testesunitarios.matchers;

import static java.util.Calendar.MONDAY;

public class PersonalMatchers {

	public static DiaSemanaMatcher caiEm(int diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}

	public static DiaSemanaMatcher caiNumaSegunda() {
		return caiEm(MONDAY);
	}

}

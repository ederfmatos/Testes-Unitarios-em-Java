package com.ederfmatos.testesunitarios.matchers;

import static java.util.Calendar.MONDAY;

public class PersonalMatchers {

	public static DiaSemanaMatcher caiEm(int diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}

	public static DateMatchers ehHoje() {
		return ehHojeMaisDias(0);
	}

	public static DateMatchers ehAmanha() {
		return ehHojeMaisDias(1);
	}
	
	public static DateMatchers ehHojeMaisDias(int dias) {
		return new DateMatchers(dias);
	}
	
	public static DiaSemanaMatcher caiNumaSegunda() {
		return new DiaSemanaMatcher(MONDAY);
	}

}

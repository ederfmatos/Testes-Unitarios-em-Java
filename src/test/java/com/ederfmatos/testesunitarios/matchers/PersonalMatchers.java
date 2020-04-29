package com.ederfmatos.testesunitarios.matchers;

import static java.util.Calendar.MONDAY;

public class PersonalMatchers {

	public static DiaSemanaMatcher caiEm(int diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}

	public static DiaSemanaMatcher caiNumaSegunda() {
		return caiEm(MONDAY);
	}

	public static DateMatchers foiOntem() {
		return ehHojeMaisDias(-1);
	}
	
	public static DateMatchers ehHoje() {
		return ehHojeMaisDias(0);
	}

	public static DateMatchers ehAmanha() {
		return ehHojeMaisDias(1);
	}
	
	public static DateMatchers ehDaquiUmaSemana() {
		return ehHojeMaisDias(7);
	}
	
	public static DateMatchers ehDaquiDuasSemanas() {
		return ehHojeMaisDias(14);
	}

	public static DateMatchers ehHojeMaisDias(int dias) {
		return new DateMatchers(dias);
	}

}

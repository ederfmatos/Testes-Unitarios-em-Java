package com.ederfmatos.testesunitarios.matchers;

import static com.ederfmatos.testesunitarios.utils.DataUtils.isMesmaData;
import static com.ederfmatos.testesunitarios.utils.DataUtils.obterDataComDiferencaDias;

import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class DateMatchers extends TypeSafeDiagnosingMatcher<Date> {

	private final int differenceDays;

	public DateMatchers(int differenceDays) {
		this.differenceDays = differenceDays;
	}

	@Override
	public void describeTo(Description description) {
	}

	@Override
	protected boolean matchesSafely(Date date, Description mismatchDescription) {
		return isMesmaData(date, obterDataComDiferencaDias(differenceDays));
	}

}

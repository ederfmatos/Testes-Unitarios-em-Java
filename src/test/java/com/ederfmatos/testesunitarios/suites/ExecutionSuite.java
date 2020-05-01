package com.ederfmatos.testesunitarios.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ederfmatos.testesunitarios.servicos.CalculadoraTest;
import com.ederfmatos.testesunitarios.servicos.CalculoValorLocacaoTest;
import com.ederfmatos.testesunitarios.servicos.LocacaoServiceTest;

//@RunWith(Suite.class)
@SuiteClasses({ CalculadoraTest.class, CalculoValorLocacaoTest.class, LocacaoServiceTest.class })
public class ExecutionSuite {

}

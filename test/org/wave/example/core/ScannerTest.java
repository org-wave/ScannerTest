package org.wave.example.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wave.scanner.enums.ErrorEnum;
import org.wave.scanner.exceptions.ScannerException;
import org.wave.utils.file.FileUtil;
import org.wave.utils.reflection.ReflectionUtil;

public class ScannerTest {

	private File docs;

	private org.wave.scanner.core.Scanner scanner;

	@Before
	public void setUp() {
		this.docs = new File("docs");
		assertFalse(this.docs.exists());

		WeldContainer container = new Weld().initialize();
		this.scanner = container.instance().select(org.wave.scanner.core.Scanner.class).get();
	}

	@Test(expected = ScannerException.class)
	public void deveLancarExcecaoQuandoNaoHouverAQuantidadeEsperadaDeArgumentosException() throws ScannerException {
		Field field = ReflectionUtil.getField("arguments", Scanner.class);
		ReflectionUtil.set(new String[] {}, field, this.scanner);

		this.scanner.scan(null);
	}

	@Test
	public void deveLancarExcecaoQuandoNaoHouverAQuantidadeEsperadaDeArgumentos() {
		Field field = ReflectionUtil.getField("arguments", Scanner.class);
		ReflectionUtil.set(new String[] {}, field, this.scanner);

		try {
			this.scanner.scan(null);
		} catch (ScannerException e) {
			assertEquals(ErrorEnum.INVALID_NUMBER_OF_ARGUMENTS.getMessage(), e.getMessage());
		}
	}

	@Test
	public void devePreencherAPastaDOCSQuandoHouverClassesEmPacotes() throws ScannerException {
		Field field = ReflectionUtil.getField("arguments", Scanner.class);
		ReflectionUtil.set(new String[] { "src", "test", "docs" }, field, this.scanner);

		this.scanner.scan(null);

		assertTrue(this.docs.exists());
		assertTrue(this.docs.isDirectory());

		File[] directories = this.docs.listFiles();
		assertEquals(4, directories.length);

		File flowsFolder = directories[0];
		assertEquals("flows", flowsFolder.getName());

		File[] flows = flowsFolder.listFiles();
		assertEquals(1, flows.length);
		for (File flow : flows) {
			assertEquals("acaoobjeto.xhtml", flow.getName());
		}
		
		File othersFolder = directories[1];
		assertEquals("others", othersFolder.getName());
		
		File[] others = othersFolder.listFiles();
		assertEquals(1, others.length);
		for (File other : others) {
			assertEquals("scanner.xhtml", other.getName());
		}

		File reportsFolder = directories[2];
		assertEquals("reports", reportsFolder.getName());

		File[] reports = reportsFolder.listFiles();
		assertEquals(1, reports.length);
		for (File report : reports) {
			assertEquals("objetoporcriterio.xhtml", report.getName());
		}

		File tasksFolder = directories[3];
		assertEquals("tasks", tasksFolder.getName());

		File[] tasks = tasksFolder.listFiles();
		assertEquals(1, tasks.length);
		for (File task : tasks) {
			assertEquals("acaoobjeto.xhtml", task.getName());
		}
	}

	@After
	public void tearDown() {
		if (this.docs.exists()) {
			assertTrue(FileUtil.delete(this.docs));
			assertFalse(this.docs.exists());
		}
	}

}

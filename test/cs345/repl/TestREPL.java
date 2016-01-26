package cs345.repl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestREPL {
	public static final String SAMPLES_DIR = "samples";

	protected String filename;
	protected String input;
	protected String expectedOutput;

	protected PrintStream save_stdout;
	protected PrintStream save_stderr;

	protected ByteArrayOutputStream stdout;
	protected ByteArrayOutputStream stderr;

	public TestREPL(String filename, String input, String expectedOutput) {
		this.filename = filename;
		this.expectedOutput = expectedOutput;
		this.input = input;
	}

	@Before
	public void setup() {
		save_stdout = System.out;
		save_stderr = System.err;
		stdout = new ByteArrayOutputStream();
		stderr = new ByteArrayOutputStream();
		System.setOut(new PrintStream(stdout));
		System.setErr(new PrintStream(stderr));
	}

	@After
	public void teardown() {
		System.setOut(save_stdout);
		System.setErr(save_stderr);
	}

	@Test
	public void testInputOutputPair() throws IOException {
		JavaREPL.exec(new StringReader(input));
		// combine errors first then output
		String output = stdout.toString();
		output = output.replaceAll("> ", ""); // kill the prompt from output
		assertEquals(expectedOutput, output+stderr.toString());
	}

	@Parameterized.Parameters(name="{0}:{1}")
	public static Collection<Object[]> findInputFiles() throws IOException {
		return findTestCasesInFolder(SAMPLES_DIR);
	}

	protected static Collection<Object[]> findTestCasesInFolder(String folder) throws IOException {
		URL testFolder = TestREPL.class.getClassLoader().getResource(folder);
		if ( testFolder==null ) {
			System.err.println("Can't find sample .java file directory.");
			return Collections.emptyList();
		}
		Collection<Object[]> result = new ArrayList<>();
		// only feed test methods with .java source files.
		File dir = new File(testFolder.getPath());
		File[] files = dir.listFiles();
		if ( files!=null ) {
			for (File f : files) {
				if ( f.getName().endsWith(".java") ) {
					Object[] args = new Object[3];
					args[0] = f.getName();
					args[1] = readFile(f.getPath());
					String outputFilename = replaceFileSuffix(f.getPath(), ".output");
					args[2] = readFile(outputFilename);
					result.add(args);
				}
			}
		}
		return result;
	}

	public static String readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}

	/** e.g., replaceFileSuffix("foo.java", ".output") */
	public static String replaceFileSuffix(String s, String suffix) {
		if ( s==null || suffix==null ) return s;
		int dot = s.lastIndexOf('.');
		return s.substring(0,dot)+suffix;
	}
}

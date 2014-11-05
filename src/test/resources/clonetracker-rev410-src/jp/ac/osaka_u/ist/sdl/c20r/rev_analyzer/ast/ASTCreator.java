package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.JavaLexer;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.Lexer;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.Token;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ASTCreator {

	/**
	 * ��͑Ώۂ̃\�[�X�t�@�C��
	 */
	private final String filePath;

	/**
	 * ��͂������ʓ���ꂽAST�̃��[�g�m�[�h
	 */
	private final CompilationUnit root;

	/**
	 * �g�[�N�����X�g
	 */
	private final List<Token> tokens;

	/**
	 * ���s����
	 */
	private final String lineSeparator = System.getProperty("line.separator");
	
	public static void main(String[] args) throws Exception {
		final ASTCreator creator = new ASTCreator(args[0]);
		final CompilationUnit ast = creator.getRoot();
		System.out.println();
	}

	public ASTCreator(String filePath) throws FileNotFoundException {
		this.filePath = filePath;
		final String src = getSrc(filePath);
		this.root = createAST(src);
		this.tokens = getTokenList(src);
	}

	private String getSrc(final String filePath) throws FileNotFoundException {
		BufferedReader br = null;

		try {
			StringBuilder builder = new StringBuilder();
			br = new BufferedReader(new FileReader(new File(filePath)));
			String line;

			while ((line = br.readLine()) != null) {
				builder.append(line + lineSeparator);
			}

			return builder.toString();

		} catch (FileNotFoundException e1) {
			// �t�@�C�����Ȃ��ꍇ�͍폜���ꂽ���̂ł��邽�ߗ�O���X���[���Ĕ�����
			throw (e1);

		} catch (Exception e2) {
			e2.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// here should't be reached!!
		return null;
	}

	private CompilationUnit createAST(String src) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);

		parser.setSource(src.toCharArray());

		return (CompilationUnit) parser.createAST(new NullProgressMonitor());
	}

	private List<Token> getTokenList(final String src) {
		final Lexer lexer = new JavaLexer(new StringReader(src));
		return lexer.runLexicalAnalysis();
	}

	/**
	 * ��͑Ώۃt�@�C���p�X���擾
	 * 
	 * @return
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * ���[�g�m�[�h���擾
	 * 
	 * @return
	 */
	public CompilationUnit getRoot() {
		return root;
	}

	/**
	 * �g�[�N���̃��X�g���擾����
	 * 
	 * @return
	 */
	public List<Token> getTokens() {
		return Collections.unmodifiableList(tokens);
	}

}

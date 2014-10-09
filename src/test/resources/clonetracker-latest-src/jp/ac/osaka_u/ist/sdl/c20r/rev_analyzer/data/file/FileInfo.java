package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.file;

import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.Token;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.AbstractElementInfo;

public class FileInfo extends AbstractElementInfo {

	/**
	 * このファイルを所有するリビジョンのID
	 */
	private final long ownerRevisionId;

	/**
	 * ファイル名
	 */
	private final String name;

	/**
	 * ファイルの絶対パス
	 */
	private final String path;

	/**
	 * 所有するブロックの数
	 */
	private final int blocks;

	/**
	 * トークンリスト
	 */
	private final List<Token> tokens;

	public FileInfo(long id, long ownerRevisionId, String name, String path,
			int blocks, List<Token> tokens) {
		super(id);
		this.ownerRevisionId = ownerRevisionId;
		this.name = name;
		this.path = path;
		this.blocks = blocks;
		this.tokens = tokens;
	}

	public long getOwnerRevisionId() {
		return ownerRevisionId;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public int getBlocks() {
		return blocks;
	}

	public final List<Token> getTokens() {
		return Collections.unmodifiableList(this.tokens);
	}

}

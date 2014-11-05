package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data;

public class RetrievedFileInfo extends AbstractRetrievedElementInfo {

	/**
	 * ファイル名
	 */
	private final String name;

	/**
	 * ファイルパス
	 */
	private final String path;

	public RetrievedFileInfo(final long id,
			final String name, final String path) {
		super(id);
		this.name = name;
		this.path = path;
	}
	
	/**
	 * ファイル名を取得
	 * @return
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * ファイルパスを取得
	 * @return
	 */
	public final String getPath() {
		return path;
	}

}

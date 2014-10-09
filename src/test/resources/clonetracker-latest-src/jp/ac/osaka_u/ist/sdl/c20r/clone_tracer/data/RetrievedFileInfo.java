package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data;

public class RetrievedFileInfo extends AbstractRetrievedElementInfo {

	/**
	 * �t�@�C����
	 */
	private final String name;

	/**
	 * �t�@�C���p�X
	 */
	private final String path;

	public RetrievedFileInfo(final long id,
			final String name, final String path) {
		super(id);
		this.name = name;
		this.path = path;
	}
	
	/**
	 * �t�@�C�������擾
	 * @return
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * �t�@�C���p�X���擾
	 * @return
	 */
	public final String getPath() {
		return path;
	}

}

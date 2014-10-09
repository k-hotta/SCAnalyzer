package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data;

public abstract class AbstractRetrievedElementInfo implements
		Comparable<AbstractRetrievedElementInfo> {
	
	/**
	 * �v�f��ID
	 */
	private final long id;
	
	public AbstractRetrievedElementInfo(final long id) {
		this.id = id;
	}
	
	/**
	 * ID���擾����
	 * @return
	 */
	public final long getId() {
		return id;
	}

	/**
	 * �v�f��ID�̔�r�ŏ����t��
	 */
	@Override
	public int compareTo(AbstractRetrievedElementInfo target) {
		return ((Long) this.id).compareTo(target.getId());
	}

}

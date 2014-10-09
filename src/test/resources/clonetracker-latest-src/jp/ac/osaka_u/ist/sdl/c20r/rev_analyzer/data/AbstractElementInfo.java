package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

public abstract class AbstractElementInfo implements Comparable<AbstractElementInfo> {

	protected final long id;
	
	public AbstractElementInfo(long id) {
		this.id = id;
	}
	
	public final long getId() {
		return id;
	}
	
	@Override
	public int compareTo(AbstractElementInfo o) {
		return ((Long) id).compareTo(o.getId());
	}

}

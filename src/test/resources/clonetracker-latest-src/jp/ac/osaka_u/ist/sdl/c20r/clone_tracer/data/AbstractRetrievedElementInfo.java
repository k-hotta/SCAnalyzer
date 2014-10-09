package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data;

public abstract class AbstractRetrievedElementInfo implements
		Comparable<AbstractRetrievedElementInfo> {
	
	/**
	 * —v‘f‚ÌID
	 */
	private final long id;
	
	public AbstractRetrievedElementInfo(final long id) {
		this.id = id;
	}
	
	/**
	 * ID‚ğæ“¾‚·‚é
	 * @return
	 */
	public final long getId() {
		return id;
	}

	/**
	 * —v‘f‚ÌID‚Ì”äŠr‚Å‡˜•t‚¯
	 */
	@Override
	public int compareTo(AbstractRetrievedElementInfo target) {
		return ((Long) this.id).compareTo(target.getId());
	}

}

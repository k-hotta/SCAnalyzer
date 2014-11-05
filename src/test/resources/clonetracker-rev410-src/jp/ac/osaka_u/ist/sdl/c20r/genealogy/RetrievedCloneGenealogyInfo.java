package jp.ac.osaka_u.ist.sdl.c20r.genealogy;

import java.util.ArrayList;
import java.util.List;

public class RetrievedCloneGenealogyInfo implements
		Comparable<RetrievedCloneGenealogyInfo> {

	private final long id;

	private final long startRev;

	private final long endRev;

	private final List<Long> pairs;

	private final int hashChanged;

	private final int addedElements;

	private final int deletedElements;

	private final int deletedElementsInFileDel;

	private final int addedRevs;

	private final int deletedRevs;

	private final int deletedFileDelRevs;

	public RetrievedCloneGenealogyInfo(final long id, final long startRev,
			final long endRev, final String pairsStr, final int hashChanged,
			final int addedElements, final int deletedElements,
			final int deletedElementsInFileDel, final int addedRevs,
			final int deletedRevs, final int deletedFileDelRevs) {
		this.id = id;
		this.startRev = startRev;
		this.endRev = endRev;
		this.pairs = new ArrayList<Long>();
		detectPairs(pairsStr);
		this.hashChanged = hashChanged;
		this.addedElements = addedElements;
		this.deletedElements = deletedElements;
		this.deletedElementsInFileDel = deletedElementsInFileDel;
		this.addedRevs = addedRevs;
		this.deletedRevs = deletedRevs;
		this.deletedFileDelRevs = deletedFileDelRevs;
	}
	
	private void detectPairs(final String pairsStr) {
		final String[] splited = pairsStr.split(",");
		for (final String str : splited) {
			pairs.add(Long.parseLong(str));
		}
	}

	
	public long getId() {
		return id;
	}

	public long getStartRev() {
		return startRev;
	}

	public long getEndRev() {
		return endRev;
	}

	public List<Long> getPairs() {
		return pairs;
	}

	public int getHashChanged() {
		return hashChanged;
	}

	public int getAddedElements() {
		return addedElements;
	}

	public int getDeletedElements() {
		return deletedElements;
	}

	public int getDeletedElementsInFileDel() {
		return deletedElementsInFileDel;
	}

	public int getAddedRevs() {
		return addedRevs;
	}

	public int getDeletedRevs() {
		return deletedRevs;
	}

	public int getDeletedFileDelRevs() {
		return deletedFileDelRevs;
	}
	
	@Override
	public int compareTo(RetrievedCloneGenealogyInfo o) {
		return ((Long) id).compareTo(o.getId());
	}

}

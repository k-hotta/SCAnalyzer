package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.AbstractRetrievedElementInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;

public class RetrievedCloneSetInfo extends AbstractRetrievedElementInfo {

	private static AtomicLong count = new AtomicLong(0);

	private final int hash;

	private final Set<RetrievedBlockInfo> elements;

	public RetrievedCloneSetInfo(final int hash) {
		super(count.getAndIncrement());
		this.hash = hash;
		this.elements = new TreeSet<RetrievedBlockInfo>();
	}

	public RetrievedCloneSetInfo(final long id, final int hash) {
		super(id);
		this.hash = hash;
		this.elements = new TreeSet<RetrievedBlockInfo>();
	}

	public final int getHash() {
		return hash;
	}

	public final Set<RetrievedBlockInfo> getAllElements() {
		return Collections.unmodifiableSet(elements);
	}

	public final RetrievedBlockInfo getElement(final long id) {
		for (RetrievedBlockInfo element : elements) {
			if (element.getId() == id) {
				return element;
			}
		}

		return null;
	}

	public final int getCount() {
		return this.elements.size();
	}

	public final void addElement(final RetrievedBlockInfo element) {
		this.elements.add(element);
	}

	public final void addElements(final Collection<RetrievedBlockInfo> elements) {
		this.elements.addAll(elements);
	}

	public final boolean containsAll(final RetrievedCloneSetInfo anotherCloneSet) {
		if (this.getCount() != anotherCloneSet.getCount()) {
			return false;
		}

		final Set<RetrievedBlockInfo> containedElements = new HashSet<RetrievedBlockInfo>();
		for (final RetrievedBlockInfo thisElement : this.getAllElements()) {
			final long id = thisElement.getId();
			for (final RetrievedBlockInfo anotherElement : anotherCloneSet
					.getAllElements()) {
				if (anotherElement.getParentUnitId() == id) {
					containedElements.add(anotherElement);
					break;
				}
			}
		}

		return anotherCloneSet.getCount() == containedElements.size();
	}

	public final String convertElementsIntoString() {
		final StringBuilder builder = new StringBuilder();
		for (final RetrievedBlockInfo element : elements) {
			builder.append(element.getId());
			builder.append(",");
		}
		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}

}

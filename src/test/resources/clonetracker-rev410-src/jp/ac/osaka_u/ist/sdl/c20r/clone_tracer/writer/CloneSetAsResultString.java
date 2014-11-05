package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.writer;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class CloneSetAsResultString implements
		Comparable<CloneSetAsResultString> {

	private final long cloneId;

	private final Map<Integer, String> lines;

	private int count;

	public CloneSetAsResultString(final long cloneId, final String line) {
		this.cloneId = cloneId;
		this.lines = new TreeMap<Integer, String>();
		this.count = 0;
		addLine(line);
	}

	public void addLine(final String line) {
		this.lines.put(count++, line);
	}

	public Map<Integer, String> getLines() {
		return Collections.unmodifiableMap(lines);
	}

	public long getCloneId() {
		return cloneId;
	}

	@Override
	public int compareTo(CloneSetAsResultString another) {
		return ((Long) this.cloneId).compareTo(another.getCloneId());
	}

}

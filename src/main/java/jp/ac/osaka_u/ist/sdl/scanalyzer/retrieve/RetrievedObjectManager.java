package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

/**
 * 
 * @author k-hotta
 *
 * @param <E>
 */
public class RetrievedObjectManager<E extends IProgramElement> {

	private final Map<Long, Segment<E>> segments = new TreeMap<>();

	private final Map<Long, CodeFragment<E>> codeFragments = new TreeMap<>();

	private final Map<Long, CloneClass<E>> cloneClasses = new TreeMap<>();

	private final Map<Long, SourceFile<E>> sourceFiles = new TreeMap<>();

	private final Map<Long, CloneClassMapping<E>> cloneClassMappings = new TreeMap<>();

	private final Map<Long, CodeFragmentMapping<E>> codeFragmentMappings = new TreeMap<>();

	private final Map<Long, Revision> revisions = new TreeMap<>();

	private final Map<Long, Version<E>> versions = new TreeMap<>();

	private final Map<Long, CloneGenealogy<E>> genealogies = new TreeMap<>();

	public final Map<Long, Segment<E>> getSegments() {
		return Collections.unmodifiableMap(segments);
	}

	public final Segment<E> getSegment(final long id) {
		return this.segments.get(id);
	}

	public final Segment<E> add(final Segment<E> segment) {
		return this.segments.put(segment.getId(), segment);
	}

	public final Map<Long, CodeFragment<E>> getCodeFragments() {
		return Collections.unmodifiableMap(codeFragments);
	}

	public final CodeFragment<E> getCodeFragment(final long id) {
		return this.codeFragments.get(id);
	}

	public final CodeFragment<E> add(final CodeFragment<E> codeFragment) {
		return this.codeFragments.put(codeFragment.getId(), codeFragment);
	}

	public final Map<Long, CloneClass<E>> getCloneClasses() {
		return Collections.unmodifiableMap(cloneClasses);
	}

	public final CloneClass<E> getCloneClass(final long id) {
		return this.cloneClasses.get(id);
	}

	public CloneClass<E> add(final CloneClass<E> cloneClass) {
		return this.cloneClasses.put(cloneClass.getId(), cloneClass);
	}

	public final Map<Long, SourceFile<E>> getSourceFiles() {
		return Collections.unmodifiableMap(sourceFiles);
	}

	public final SourceFile<E> getSourceFile(final long id) {
		return this.sourceFiles.get(id);
	}

	public SourceFile<E> add(final SourceFile<E> sourceFile) {
		return this.sourceFiles.put(sourceFile.getId(), sourceFile);
	}

	public final Map<Long, CloneClassMapping<E>> getCloneClassMappings() {
		return Collections.unmodifiableMap(cloneClassMappings);
	}

	public final CloneClassMapping<E> getCloneClassMapping(final long id) {
		return this.cloneClassMappings.get(id);
	}

	public CloneClassMapping<E> add(final CloneClassMapping<E> cloneClassMapping) {
		return this.cloneClassMappings.put(cloneClassMapping.getId(),
				cloneClassMapping);
	}

	public final Map<Long, CodeFragmentMapping<E>> getCodeFragmentMappings() {
		return Collections.unmodifiableMap(codeFragmentMappings);
	}

	public final CodeFragmentMapping<E> getCodeFragmentMapping(final long id) {
		return this.codeFragmentMappings.get(id);
	}

	public CodeFragmentMapping<E> add(
			final CodeFragmentMapping<E> codeFragmentMapping) {
		return this.codeFragmentMappings.put(codeFragmentMapping.getId(),
				codeFragmentMapping);
	}

	public final Map<Long, Revision> getRevisions() {
		return Collections.unmodifiableMap(revisions);
	}

	public final Revision getRevision(final long id) {
		return this.revisions.get(id);
	}

	public Revision add(final Revision revision) {
		return this.revisions.put(revision.getId(), revision);
	}

	public final Map<Long, Version<E>> getVersions() {
		return Collections.unmodifiableMap(versions);
	}

	public final Version<E> getVersion(final long id) {
		return this.versions.get(id);
	}

	public Version<E> add(final Version<E> version) {
		return this.versions.put(version.getId(), version);
	}

	public final Map<Long, CloneGenealogy<E>> getGenealogies() {
		return Collections.unmodifiableMap(genealogies);
	}

	public final CloneGenealogy<E> getGenealogy(final long id) {
		return this.genealogies.get(id);
	}

	public CloneGenealogy<E> add(final CloneGenealogy<E> cloneGenealogy) {
		return this.genealogies.put(cloneGenealogy.getId(), cloneGenealogy);
	}

	/**
	 * Clear all the retrieved objects.
	 */
	public void clear() {
		segments.clear();
		codeFragments.clear();
		cloneClasses.clear();
		sourceFiles.clear();
		cloneClassMappings.clear();
		codeFragmentMappings.clear();
		revisions.clear();
		versions.clear();
		genealogies.clear();
	}

}

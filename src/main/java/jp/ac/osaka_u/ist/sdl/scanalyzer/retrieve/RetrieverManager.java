package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.WorkerManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneModification;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;

public class RetrieverManager<E extends IProgramElement> {

	private final RetrievedObjectManager<E> manager;

	private final IRetriever<E, DBCloneGenealogy, CloneGenealogy<E>> genealogyRetriever;

	private final IRetriever<E, DBVersion, Version<E>> versionRetriever;

	private final IRetriever<E, DBRevision, Revision> revisionRetriever;

	private final IRetriever<E, DBSourceFile, SourceFile<E>> sourceFileRetriever;

	private final IRetriever<E, DBCloneClass, CloneClass<E>> cloneClassRetriever;

	private final IRetriever<E, DBCloneClassMapping, CloneClassMapping<E>> cloneClassMappingRetriever;

	private final IRetriever<E, DBCodeFragment, CodeFragment<E>> codeFragmentRetriever;

	private final IRetriever<E, DBCodeFragmentMapping, CodeFragmentMapping<E>> codeFragmentMappingRetriever;

	private final IRetriever<E, DBCloneModification, CloneModification<E>> cloneModificationRetriever;

	private final IRetriever<E, DBSegment, Segment<E>> segmentRetriever;

	public RetrieverManager(final RetrieveMode mode,
			final WorkerManager<E> workerManager) {
		this.manager = new RetrievedObjectManager<E>();

		switch (mode) {
		case VOLATILE:
			final VolatileGenealogyRetriever<E> volatileGenealogyRetriever = new VolatileGenealogyRetriever<E>(
					manager);
			final VolatileVersionRetriever<E> volatileVersionRetriever = new VolatileVersionRetriever<E>(
					manager);
			final VolatileRevisionRetriever<E> volatileRevisionRetriever = new VolatileRevisionRetriever<E>(
					manager);
			final VolatileSourceFileRetriever<E> volatileSourceFileRetriever = new VolatileSourceFileRetriever<E>(
					manager, workerManager.getFileContentProvider(),
					workerManager.getFileParser());
			final VolatileCloneClassRetriever<E> volatileCloneClassRetriever = new VolatileCloneClassRetriever<E>(
					manager);
			final VolatileCloneClassMappingRetriever<E> volatileCloneClassMappingRetriever = new VolatileCloneClassMappingRetriever<E>(
					manager);
			final VolatileCodeFragmentRetriever<E> volatileCodeFragmentRetriever = new VolatileCodeFragmentRetriever<E>(
					manager);
			final VolatileCodeFragmentMappingRetriever<E> volatileCodeFragmentMappingRetriever = new VolatileCodeFragmentMappingRetriever<E>(
					manager);
			final VolatileSegmentRetriever<E> volatileSegmentRetriever = new VolatileSegmentRetriever<E>(
					manager);

			volatileGenealogyRetriever
					.setCloneClassMappingRetriever(volatileCloneClassMappingRetriever);
			volatileGenealogyRetriever
					.setCodeFragmentMappingRetriever(volatileCodeFragmentMappingRetriever);
			volatileGenealogyRetriever
					.setVersionRetriever(volatileVersionRetriever);

			volatileVersionRetriever
					.setRevisionRetriever(volatileRevisionRetriever);

			volatileCloneClassRetriever
					.setCodeFragmentRetriever(volatileCodeFragmentRetriever);
			volatileCloneClassRetriever
					.setVersionRetriever(volatileVersionRetriever);

			volatileCloneClassMappingRetriever
					.setCloneClassRetriever(volatileCloneClassRetriever);

			volatileCodeFragmentRetriever
					.setSegmentRetriever(volatileSegmentRetriever);

			volatileCodeFragmentMappingRetriever
					.setCodeFragmentRetriever(volatileCodeFragmentRetriever);

			volatileSegmentRetriever
					.setSourceFileRetriever(volatileSourceFileRetriever);
			volatileSegmentRetriever
					.setVersionRetriever(volatileVersionRetriever);

			this.genealogyRetriever = volatileGenealogyRetriever;
			this.versionRetriever = volatileVersionRetriever;
			this.revisionRetriever = volatileRevisionRetriever;
			this.sourceFileRetriever = volatileSourceFileRetriever;
			this.cloneClassRetriever = volatileCloneClassRetriever;
			this.cloneClassMappingRetriever = volatileCloneClassMappingRetriever;
			this.codeFragmentRetriever = volatileCodeFragmentRetriever;
			this.codeFragmentMappingRetriever = volatileCodeFragmentMappingRetriever;
			this.segmentRetriever = volatileSegmentRetriever;

			/*
			 * TODO implement volatile retriever for clone modifications
			 */
			this.cloneModificationRetriever = null;

			break;

		case PERSIST:
			this.genealogyRetriever = new PersistGenealogyRetriever<E>(manager);
			this.versionRetriever = new PersistVersionRetriever<E>(manager);
			this.revisionRetriever = new PersistRevisionRetriever<E>(manager);
			this.sourceFileRetriever = new PersistSourceFileRetriever<E>(
					manager);
			this.cloneClassRetriever = new PersistCloneClassRetriever<E>(
					manager);
			this.cloneClassMappingRetriever = new PersistCloneClassMappingRetriever<E>(
					manager);
			this.codeFragmentRetriever = new PersistCodeFragmentRetriever<E>(
					manager);
			this.codeFragmentMappingRetriever = new PersistCodeFragmentMappingRetriever<E>(
					manager);
			this.segmentRetriever = new PersistSegmentRetriever<E>(manager);
			this.cloneModificationRetriever = new PersistCloneModificationRetriever<E>(
					manager);
			break;

		default:
			throw new IllegalStateException("unknown mode");
		}
	}

	public final IRetriever<E, DBCloneGenealogy, CloneGenealogy<E>> getGenealogyRetriever() {
		return genealogyRetriever;
	}

	public final IRetriever<E, DBVersion, Version<E>> getVersionRetriever() {
		return versionRetriever;
	}

	public final IRetriever<E, DBRevision, Revision> getRevisionRetriever() {
		return revisionRetriever;
	}

	public final IRetriever<E, DBSourceFile, SourceFile<E>> getSourceFileRetriever() {
		return sourceFileRetriever;
	}

	public final IRetriever<E, DBCloneClass, CloneClass<E>> getCloneClassRetriever() {
		return cloneClassRetriever;
	}

	public final IRetriever<E, DBCloneClassMapping, CloneClassMapping<E>> getCloneClassMappingRetriever() {
		return cloneClassMappingRetriever;
	}

	public final IRetriever<E, DBCodeFragment, CodeFragment<E>> getCodeFragmentRetriever() {
		return codeFragmentRetriever;
	}

	public final IRetriever<E, DBCodeFragmentMapping, CodeFragmentMapping<E>> getCodeFragmentMappingRetriever() {
		return codeFragmentMappingRetriever;
	}

	public final IRetriever<E, DBSegment, Segment<E>> getSegmentRetriever() {
		return segmentRetriever;
	}

	public final IRetriever<E, DBCloneModification, CloneModification<E>> getCloneModificationRetriever() {
		return cloneModificationRetriever;
	}

}

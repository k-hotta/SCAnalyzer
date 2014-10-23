package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDBElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class DBElementManager<T extends IDBElement> {

	private final Logger logger;

	private final ConcurrentMap<Long, T> elements;

	DBElementManager(final Class<T> clazz) {
		this.logger = LogManager.getLogger(clazz);
		this.elements = new ConcurrentSkipListMap<Long, T>();
	}

	ConcurrentMap<Long, T> getElements() {
		return elements;
	}

	T putIfAbsent(final T element) {
		logger.trace("the element " + element.getId() + " was put");
		return this.elements.putIfAbsent(element.getId(), element);
	}

}

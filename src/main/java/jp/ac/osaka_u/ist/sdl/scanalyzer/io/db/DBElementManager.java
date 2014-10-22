package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDBElement;

class DBElementManager<T extends IDBElement> {

	private final Logger logger;

	private final int maximumElementsCount;

	private final ConcurrentMap<Long, T> elements;

	private final Queue<Long> putOrder;

	DBElementManager(final Class<T> clazz, final int maximumElementsCount) {
		this.logger = LogManager.getLogger(clazz);
		this.maximumElementsCount = maximumElementsCount;
		this.elements = new ConcurrentSkipListMap<Long, T>();
		this.putOrder = new ConcurrentLinkedQueue<Long>();
	}

	ConcurrentMap<Long, T> getElements() {
		return elements;
	}

	T putIfAbsent(final T element) {
		T result = element;
		if (!elements.containsKey(element.getId())) {
			this.elements.put(element.getId(), element);
			this.putOrder.offer(element.getId());
			logger.trace("the element " + element.getId() + " was put");

			synchronized (elements) {
				if (this.elements.size() > maximumElementsCount) {
					logger.trace("the number of stored elements "
							+ this.elements.size()
							+ " is greater than given threshold");
					logger.trace("old elements will be removed");

					int half = maximumElementsCount / 2;
					int currentSize = this.elements.size();

					while (currentSize > half) {
						long toBeRemoved = putOrder.poll();
						this.elements.remove(toBeRemoved);

						currentSize = this.elements.size();
					}

					logger.trace("now there are " + currentSize
							+ " elements stored");
				}
			}
		}

		return result;
	}
	
}

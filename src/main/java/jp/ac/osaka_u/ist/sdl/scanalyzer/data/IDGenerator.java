package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IDGenerator {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(IDGenerator.class);

	/**
	 * A map that has a class and its corresponding ID generator
	 */
	private static final Map<Class<?>, AtomicLong> generators = new HashMap<Class<?>, AtomicLong>();

	/**
	 * Generate the next id for the given class. <br>
	 * If the corresponding generator for the given class has not been
	 * initialized, the generator will be initialized with zero.
	 * 
	 * @param clazz
	 *            the class for which the next id will be generated
	 * @return generated id
	 */
	public static synchronized long generate(final Class<?> clazz) {
		if (!generators.containsKey(clazz)) {
			generators.put(clazz, new AtomicLong(0));
		}

		return generators.get(clazz).getAndIncrement();
	}

	/**
	 * Initialize the corresponding generator for the given class with the
	 * specified long value. <br>
	 * 
	 * @param clazz
	 *            the class whose generator will be initialized
	 * @param initial
	 *            the initial value of id
	 */
	public static synchronized void initialize(final Class<?> clazz, final long initial) {
		if (generators.containsKey(clazz)) {
			generators.remove(clazz);
		}

		generators.put(clazz, new AtomicLong(initial));
		logger.trace("the ID generator for " + clazz.getName()
				+ " has been initialized with " + initial);
	}

}

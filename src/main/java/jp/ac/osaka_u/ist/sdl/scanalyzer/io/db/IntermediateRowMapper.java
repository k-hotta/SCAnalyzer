package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.j256.ormlite.dao.RawRowMapper;

public class IntermediateRowMapper<R extends InternalIntermediateDataRepresentation<?>>
		implements RawRowMapper<R> {

	private Function<Map<String, Long>, R> instantiateFunction;

	public void setInstantiateFunction(
			final Function<Map<String, Long>, R> instantiateFunction) {
		this.instantiateFunction = instantiateFunction;
	}

	@Override
	public R mapRow(String[] columnNames, String[] resultColumns)
			throws SQLException {
		final Map<String, Long> map = new TreeMap<>();

		for (int i = 0; i < columnNames.length; i++) {
			final String columnName = columnNames[i];
			final String resultColumn = resultColumns[i];

			map.put(columnName, Long.parseLong(resultColumn));
		}

		return instantiateFunction.apply(map);
	}

}

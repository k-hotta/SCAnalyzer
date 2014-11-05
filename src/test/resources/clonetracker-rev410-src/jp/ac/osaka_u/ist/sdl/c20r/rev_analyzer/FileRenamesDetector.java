package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.DataManagerManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.FileManager;

public class FileRenamesDetector {

	private Table<String, String, Double> similarityTable;

	private final double similarityThreshold;

	private final ISimilarityCalculator simCalculator;

	private final Map<String, Queue<String>> sortedAddedElement;

	private final int threadsCount;

	private final FileManager fileManager = DataManagerManager.getInstance()
			.getFileManager();

	public FileRenamesDetector(final ISimilarityCalculator simCalculator,
			final double simThreshold, final int threadsCount) {
		this.similarityTable = new Table<String, String, Double>();
		this.similarityThreshold = simThreshold;
		this.simCalculator = simCalculator;
		this.sortedAddedElement = new HashMap<String, Queue<String>>();
		this.threadsCount = threadsCount;
	}

	public Map<String, String> detectRenames(
			final Map<String, List<Token>> previousFiles,
			final List<String> deletedFilePaths,
			final List<String> addedFilePaths) {
		final Map<String, List<Token>> deletedFiles = new TreeMap<String, List<Token>>();
		for (final String deletedFilePath : deletedFilePaths) {
			deletedFiles.put(deletedFilePath,
					previousFiles.get(deletedFilePath));
		}

		final Map<String, List<Token>> addedFiles = new TreeMap<String, List<Token>>();
		for (final String addedFilePath : addedFilePaths) {
			addedFiles
					.put(addedFilePath,
							fileManager
									.getElement(
											fileManager
													.getIdHavingSpecifiedPath(addedFilePath))
									.getTokens());
		}

		return detect(deletedFiles, addedFiles);
	}

	private Map<String, String> detect(final Map<String, List<Token>> before,
			final Map<String, List<Token>> after) {
		final Map<String, String> result = new TreeMap<String, String>();

		if (before.isEmpty() || after.isEmpty()) {
			return result;
		}

		System.out.println("\t\tfilling the similarity table ... ");
		fillSimilarityTable(before, after);

		System.out.println("\t\tsorting elements ... ");
		detectSortedAddedElementsLists(before.keySet());

		System.out.println("\t\tdetecting pairs of files ... ");

		final Map<String, String> detectedPairs = new TreeMap<String, String>();

		final List<String> unmatchedDeletedElements = new ArrayList<String>();
		unmatchedDeletedElements.addAll(before.keySet());

		final int minLength = Math.min(before.size(), after.size());

		final List<String> dummyList = new ArrayList<String>();

		while (true) {
			if (unmatchedDeletedElements.isEmpty()
					|| detectedPairs.size() >= minLength) {
				break;
			}

			dummyList.clear();
			dummyList.addAll(unmatchedDeletedElements);
			for (final String deletedElement : dummyList) {
				final String mostSimilarAddedElement = this.sortedAddedElement
						.get(deletedElement).poll();
				if (mostSimilarAddedElement == null) {
					continue;
				}

				if (detectedPairs.containsKey(mostSimilarAddedElement)) {
					final String rivalElement = detectedPairs
							.get(mostSimilarAddedElement);
					if (similarityTable.getValueAt(deletedElement,
							mostSimilarAddedElement) > similarityTable
							.getValueAt(rivalElement, mostSimilarAddedElement)) {
						// remove already detected pair and create new one
						detectedPairs.remove(mostSimilarAddedElement);
						detectedPairs.put(mostSimilarAddedElement,
								deletedElement);
						unmatchedDeletedElements.add(rivalElement);
						unmatchedDeletedElements.remove(deletedElement);
						break;
					}
				} else {
					// the first match for the added file
					detectedPairs.put(mostSimilarAddedElement, deletedElement);
					unmatchedDeletedElements.remove(deletedElement);
					break;
				}
			}
		}

		for (final Map.Entry<String, String> entry : detectedPairs.entrySet()) {
			if (similarityTable.getValueAt(entry.getValue(), entry.getKey()) >= similarityThreshold) {
				result.put(entry.getValue(), entry.getKey());
			}
		}

		return result;
	}

	private void fillSimilarityTable(Map<String, List<Token>> before,
			Map<String, List<Token>> after) {
		System.out.println("\t\t\tdeleted files : " + before.size());
		System.out.println("\t\t\tadded files : " + after.size());
		int count = 0;
		for (final Map.Entry<String, List<Token>> beforeEntry : before
				.entrySet()) {
			for (final Map.Entry<String, List<Token>> afterEntry : after
					.entrySet()) {
				System.out.print(".");
				final double similarity = simCalculator.calc(
						beforeEntry.getValue(), afterEntry.getValue());
				this.similarityTable.changeValueAt(beforeEntry.getKey(),
						afterEntry.getKey(), similarity);
				if (count++ % 100 == 0) {
					System.out.println(" " + count);
				}
			}
		}
		System.out.println();
	}

	private void detectSortedAddedElementsLists(
			final Collection<String> deletedElements) {
		for (final String deletedElement : deletedElements) {
			this.sortedAddedElement.put(deletedElement,
					detectSortedAddedElementsList(deletedElement));
		}
	}

	private Queue<String> detectSortedAddedElementsList(
			final String deletedElement) {
		final Queue<String> result = new LinkedBlockingQueue<String>();
		final Map<String, Double> addedElementsWithSimilarity = new HashMap<String, Double>();
		addedElementsWithSimilarity.putAll(this.similarityTable
				.getValuesAt(deletedElement));

		while (!addedElementsWithSimilarity.isEmpty()) {
			final String mostSimilarElement = getMostSimilarElement(addedElementsWithSimilarity);
			result.offer(mostSimilarElement);
			addedElementsWithSimilarity.remove(mostSimilarElement);
		}

		return result;
	}

	/**
	 * Get the most similar element from the input map
	 * 
	 * @param target
	 * @return
	 */
	private String getMostSimilarElement(Map<String, Double> target) {
		String result = null;
		for (final Map.Entry<String, Double> entry : target.entrySet()) {
			if (result == null) {
				result = entry.getKey();
			} else {
				if (entry.getValue() > target.get(result)) {
					result = entry.getKey();
				}
			}
		}
		return result;
	}

}

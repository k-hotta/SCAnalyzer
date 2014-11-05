package jp.ac.osaka_u.ist.sdl.c20r.csv_analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {

	private int beforeRevision;

	private int afterRevision;

	private int clonesSets;

	private int cloneElements;

	private int disappearedCloneSets;

	private int disappearedCloneElements;

	private int cloneSetsContainsElementsInDeletedFiles;

	private int cloneSetsContainsElementsNotInDeletedFiles;

	private int cloneSetsContainsBoth;
	
	private int movedBlocks;
	
	public CSVReader() {
		reset();
	}

	private void reset() {
		this.beforeRevision = 0;
		this.afterRevision = 0;
		this.clonesSets = 0;
		this.cloneElements = 0;
		this.disappearedCloneSets = 0;
		this.disappearedCloneElements = 0;
		this.cloneSetsContainsElementsInDeletedFiles = 0;
		this.cloneSetsContainsElementsNotInDeletedFiles = 0;
		this.cloneSetsContainsBoth = 0;
		this.movedBlocks = 0;
	}

	public int getBeforeRevision() {
		return beforeRevision;
	}

	public int getAfterRevision() {
		return afterRevision;
	}

	public int getCloneSetsCount() {
		return clonesSets;
	}

	public int getCloneElementsCount() {
		return cloneElements;
	}

	public int getDisappearedCloneSetsCount() {
		return disappearedCloneSets;
	}

	public int getDisappearedCloneElementsCount() {
		return disappearedCloneElements;
	}

	public int getCloneSetsContainsElementsInDeletedFiles() {
		return cloneSetsContainsElementsInDeletedFiles;
	}

	public int getCloneSetsContainsElementsNotInDeletedFiles() {
		return cloneSetsContainsElementsNotInDeletedFiles;
	}

	public int getCloneSetsContainsBoth() {
		return cloneSetsContainsBoth;
	}
	
	public int getMovedBlocks() {
		return movedBlocks;
	}
	
	public void readFile(final String filePath) {
		reset();
		BufferedReader br = null;

		try {

			br = new BufferedReader(new FileReader(new File(filePath)));
			String line = br.readLine(); // 1çsñ⁄ÇÕãÛì«Ç›
			String[] splitedLine = br.readLine().split(",");

			final int before = Integer.parseInt(splitedLine[0]);
			final int after = Integer.parseInt(splitedLine[1]);

			this.beforeRevision = before;
			this.afterRevision = after;

			line = br.readLine(); // 3çsñ⁄ÇÕãÛì«Ç›

			long currentCloneId = -1;
			boolean isDisappearedCloneSet = false;
			boolean containsElementsInDeletedFile = false;
			boolean containsElementsNotInDeletedFile = false;

			while ((line = br.readLine()) != null) {
				splitedLine = line.split(",");
				final long cloneId = Long.parseLong(splitedLine[0]);
				final int disappeared = Integer.parseInt(splitedLine[7]);
				final boolean isDisappearedCloneElement = (disappeared == 1);
				final int inDeletedFile = Integer.parseInt(splitedLine[8]);
				final boolean isInDeletedFile = (inDeletedFile == 1);
				final int moved = Integer.parseInt(splitedLine[12]);
				final boolean isMoved = (moved == 1);

				this.cloneElements++;
				
				if (isMoved) {
					movedBlocks++;
				}

				if (currentCloneId != cloneId && currentCloneId != -1) {
					this.clonesSets++;
					if (isDisappearedCloneSet) {
						this.disappearedCloneSets++;
					}
					if (containsElementsInDeletedFile) {
						this.cloneSetsContainsElementsInDeletedFiles++;
						if (containsElementsNotInDeletedFile) {
							this.cloneSetsContainsBoth++;
						}
					}
					if (containsElementsNotInDeletedFile) {
						this.cloneSetsContainsElementsNotInDeletedFiles++;
					}
					isDisappearedCloneSet = false;
					containsElementsInDeletedFile = false;
					containsElementsNotInDeletedFile = false;
				}

				if (isDisappearedCloneElement) {
					isDisappearedCloneSet = true;
					this.disappearedCloneElements++;
					if (isInDeletedFile) {
						containsElementsInDeletedFile = true;
					} else {
						containsElementsNotInDeletedFile = true;
					}
				}

				currentCloneId = cloneId;

			}
			
			if (currentCloneId != -1) {
				this.clonesSets++;
				if (isDisappearedCloneSet) {
					this.disappearedCloneSets++;
				}
				if (containsElementsInDeletedFile) {
					this.cloneSetsContainsElementsInDeletedFiles++;
					if (containsElementsNotInDeletedFile) {
						this.cloneSetsContainsBoth++;
					}
				}
				if (containsElementsNotInDeletedFile) {
					this.cloneSetsContainsElementsNotInDeletedFiles++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

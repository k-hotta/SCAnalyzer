package jp.ac.osaka_u.ist.sdl.c20r;

import org.apache.commons.cli.CommandLine;

public class Config {

	private final CommandLine cl;

	public Config(final CommandLine cl) {

		if (cl.hasOption("C")) {

			if (!cl.hasOption("f")) {
				System.out
						.println("location of database must be specified with \"-i\"");
				System.exit(0);
			}

			if (!cl.hasOption("l")) {
				System.out
						.println("location of database must be specified with \"-l\"");
				System.exit(0);
			}

			if (!cl.hasOption("n")) {
				System.out
						.println("name of database must be specified with \"-n\"");
				System.exit(0);
			}

			if (!cl.hasOption("r")) {
				System.out
						.println("path to repository must be specified with \"-r\"");
				System.exit(0);
			}

			if (!cl.hasOption("w")) {
				System.out
						.println("working directory must be specified with \"-w\"");
				System.exit(0);
			}

			if (cl.hasOption("m")) {
				try {
					Integer.valueOf(cl.getOptionValue("m"));
				} catch (Exception e) {
					System.out
							.println("minimum size of target blocks must be specified with numerical values");
					System.exit(0);
				}
			}

			if (cl.hasOption("s")) {
				try {
					Integer.valueOf(cl.getOptionValue("s"));
				} catch (Exception e) {
					System.out
							.println("start revision number must be specified with numerical values");
					System.exit(0);
				}
			}

			if (cl.hasOption("e")) {
				try {
					Integer.valueOf(cl.getOptionValue("e"));
				} catch (Exception e) {
					System.out
							.println("end revision number must be specified with numerical values");
					System.exit(0);
				}
			}

		}

		else if (cl.hasOption("R")) {

			if (!cl.hasOption("e")) {
				System.out
						.println("end revision must be specified with \"-e\"");
				System.exit(0);
			}

			if (!cl.hasOption("f")) {
				System.out.println("output file must be specified with \"-f\"");
				System.exit(0);
			}

			if (!cl.hasOption("r")) {
				System.out
						.println("path to repository must be specified with \"-r\"");
				System.exit(0);
			}

			if (!cl.hasOption("s")) {
				System.out
						.println("start revision must be specified with \"-s\"");
				System.exit(0);
			}
		}

		else if (cl.hasOption("T")) {

			if (!cl.hasOption("l")) {
				System.out
						.println("location of database must be specified with \"-l\"");
				System.exit(0);
			}

			if (!cl.hasOption("w")) {
				System.out
						.println("working directory must be specified with \"-w\"");
				System.exit(0);
			}

			if (!cl.hasOption("o")) {
				System.out
						.println("output directory must be specified with \"-o\"");
				System.exit(0);
			}

			if (cl.hasOption("th")) {
				try {
					Integer.valueOf(cl.getOptionValue("th"));
				} catch (Exception e) {
					System.out
							.println("threads count must be specified with numerical values");
					System.exit(0);
				}
			}

			if (cl.hasOption("s")) {
				try {
					Integer.valueOf(cl.getOptionValue("s"));
				} catch (Exception e) {
					System.out
							.println("start revision number must be specified with numerical values");
					System.exit(0);
				}
			}

			if (cl.hasOption("e")) {
				try {
					Integer.valueOf(cl.getOptionValue("e"));
				} catch (Exception e) {
					System.out
							.println("end revision number must be specified with numerical values");
					System.exit(0);
				}
			}

		}

		else if (cl.hasOption("U")) {

			if (!cl.hasOption("r")) {
				System.out
						.println("path to repository must be specified with \"-r\"");
				System.exit(0);
			}

			if (!cl.hasOption("i")) {
				System.out
						.println("input csv file must be specified with \"-i\"");
				System.exit(0);
			}

		}

		else if (cl.hasOption("A")) {

			if (!cl.hasOption("i")) {
				System.out
						.println("input directory must be specified with \"-i\"");
				System.exit(0);
			}

			if (!cl.hasOption("o")) {
				System.out
						.println("output csv file must be specified with \"-o\"");
				System.exit(0);
			}

			if (cl.hasOption("th")) {
				try {
					Integer.valueOf(cl.getOptionValue("th"));
				} catch (Exception e) {
					System.out
							.println("threads count must be specified with numerical values");
					System.exit(0);
				}
			}
		}

		else if (cl.hasOption("S")) {

			if (!cl.hasOption("l")) {
				System.out
						.println("location of database must be specified with \"-l\"");
				System.exit(0);
			}

			if (cl.hasOption("th")) {
				try {
					Integer.valueOf(cl.getOptionValue("th"));
				} catch (Exception e) {
					System.out
							.println("threads count must be specified with numerical values");
					System.exit(0);
				}
			}

			if (cl.hasOption("mg")) {
				if (!cl.getOptionValue("mg").equalsIgnoreCase("yes")
						&& !cl.getOptionValue("mg").equalsIgnoreCase("no")) {
					System.out
							.println("-mg must be specified with \"yes\" or \"no\"");
				}
			}

		}

		else if (cl.hasOption("G")) {

			if (!cl.hasOption("l")) {
				System.out
						.println("location of database must be specified with \"-l\"");
				System.exit(0);
			}

			if (!cl.hasOption("o")) {
				System.out
						.println("output directory must be specified with \"-o\"");
				System.exit(0);
			}

			if (cl.hasOption("th")) {
				try {
					Integer.valueOf(cl.getOptionValue("th"));
				} catch (Exception e) {
					System.out
							.println("threads count must be specified with numerical values");
					System.exit(0);
				}
			}

		}

		else {
			System.err
					.println("\"-C\", \"-T\", \"-R\", \"-A\", \"-U\", or \"-S\" must be specified.");
			System.exit(0);
		}

		this.cl = cl;
	}

	public int getEND_REVISION() {
		if (this.cl.hasOption("e")) {
			return Integer.parseInt(this.cl.getOptionValue("e"));
		} else {
			return Integer.MAX_VALUE;
		}
	}

	public String getREVISION_FILE() {
		return this.cl.getOptionValue("f");
	}

	public String getREPOSITORY_PATH() {
		return this.cl.getOptionValue("r");
	}

	public String getDB_LOCATION() {
		return this.cl.getOptionValue("l");
	}

	public String getDB_NAME() {
		return this.cl.getOptionValue("n");
	}

	public int getSTART_REVISION() {
		if (this.cl.hasOption("s")) {
			return Integer.parseInt(this.cl.getOptionValue("s"));
		} else {
			return 0;
		}
	}

	public String getWORKING_DIRECTORY() {
		return this.cl.getOptionValue("w");
	}

	public String getOUTPUT_PATH() {
		return this.cl.getOptionValue("o");
	}

	public String getANOTHER_WORKING_DIRECTORY() {
		return this.cl.getOptionValue("aw");
	}

	public int getMIN_SIZE() {
		if (this.cl.hasOption("m")) {
			return Integer.parseInt(this.cl.getOptionValue("m"));
		} else {
			return 30;
		}
	}

	public int getTHREADS_COUNT() {
		if (this.cl.hasOption("th")) {
			return Integer.valueOf(this.cl.getOptionValue("th"));
		} else {
			return 1;
		}
	}

	public String getINPUT_PATH() {
		return this.cl.getOptionValue("i");
	}

	public String getOUTPUT_FILE_PATH_FOR_TIME() {
		if (this.cl.hasOption("ot")) {
			return this.cl.getOptionValue("ot");
		} else {
			return null;
		}
	}

	public String getTARGET() {
		if (this.cl.hasOption("ta")) {
			return this.cl.getOptionValue("ta");
		} else {
			return null;
		}
	}

	public boolean getMERGE() {
		if (cl.hasOption("mg")) {
			if (cl.getOptionValue("mg").equalsIgnoreCase("yes")) {
				return true;
			}
		}
		return false;
	}

	public MODE getMODE() {
		if (this.cl.hasOption("C")) {
			return MODE.CLONE;
		} else if (this.cl.hasOption("R")) {
			return MODE.REVISION;
		} else if (this.cl.hasOption("T")) {
			return MODE.TRACKING;
		} else if (this.cl.hasOption("U")) {
			return MODE.UI;
		} else if (this.cl.hasOption("A")) {
			return MODE.ANALYZING;
		} else if (this.cl.hasOption("S")) {
			return MODE.SEQUENTIAL;
		} else if (this.cl.hasOption("G")) {
			return MODE.GRAPH;
		} else {
			return null;
		}
	}
}

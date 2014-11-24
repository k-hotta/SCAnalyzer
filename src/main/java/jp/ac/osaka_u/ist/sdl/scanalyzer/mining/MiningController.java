package jp.ac.osaka_u.ist.sdl.scanalyzer.mining;

/**
 * This is for controlling the mining procedure.
 * 
 * @author k-hotta
 *
 */
public class MiningController {

	/**
	 * The number of genealogies processed at a time.
	 */
	private final int maximumGenealogiesCount;

	/**
	 * The strategy of mining
	 */
	private final MiningStrategy strategy;

	public MiningController(final int maximumGenealogiesCount,
			final MiningStrategy stragety) {
		this.maximumGenealogiesCount = maximumGenealogiesCount;
		this.strategy = stragety;
	}
	
	public void performMining() {
		
	}

}

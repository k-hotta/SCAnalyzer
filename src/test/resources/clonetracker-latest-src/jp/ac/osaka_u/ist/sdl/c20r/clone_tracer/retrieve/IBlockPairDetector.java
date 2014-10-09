package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import java.util.Set;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.MovedBlockPairInfo;

public interface IBlockPairDetector {

	public Set<MovedBlockPairInfo> detectMovedBlockPairs();

}

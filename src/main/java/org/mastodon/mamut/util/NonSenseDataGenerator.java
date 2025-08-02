/*-
 * #%L
 * Mastodon Simulator
 * %%
 * Copyright (C) 2023 - 2025 Vladimir Ulman
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.mamut.util;

import org.mastodon.mamut.ProjectModel;
import org.mastodon.mamut.model.ModelGraph;
import org.mastodon.mamut.model.Spot;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class was supposed to generate data of a certain shapes,
 * and otherwise without any other requirement. Especially, the
 * resulting lineage tree attempts _not_ to mimic anything realistic.
 *
 * The original purpose was to benchmark Mastodon project loaders API.
 * This class is now only a legacy...
 */
public class NonSenseDataGenerator {
	public NonSenseDataGenerator(final ProjectModel projectModel,
	                             final int numberOfTimepoints,
	                             final int numberOfSpotsPerTimepoint,
	                             final boolean doLinkSpots,
	                             final int reportFromRound,
	                             final int reportEveryNthRound) {

		ReentrantReadWriteLock lock = projectModel.getModel().getGraph().getLock();
		final Spot auxSpot = projectModel.getModel().getGraph().vertexRef();
		final Spot prevSpot = projectModel.getModel().getGraph().vertexRef();
		//Edge auxEdge = projectModel.getModel().getGraph().edgeRef();

		try {
			System.out.println("GENERATOR STARTED on "+java.time.LocalTime.now());

			new ModelGraphListeners().pauseListeners();
			lock.writeLock().lock();

			final double[] coords = new double[3];

			//start generating
			if (doLinkSpots) {
				//"vertically"
				for (int cnt = 0; cnt < numberOfSpotsPerTimepoint; ++cnt) {
					for (int time = 0; time < numberOfTimepoints; ++time) {
						//create and add a spot, and link to its parent
						coords[0] = 100.0 * Math.cos(6.28 * (double)cnt / (double)numberOfSpotsPerTimepoint);
						coords[1] = 100.0 * Math.sin(6.28 * (double)cnt / (double)numberOfSpotsPerTimepoint);
						projectModel.getModel().getGraph().addVertex(auxSpot).init(time, coords, 0.87);
						if (time > 0) projectModel.getModel().getGraph().addEdge(prevSpot, auxSpot).init();
						prevSpot.refTo(auxSpot);
					}
					if (cnt > reportFromRound && (cnt % reportEveryNthRound) == 0) {
						System.out.println("added in total "+(numberOfTimepoints*(cnt+1))
								+" spots and "+((numberOfTimepoints-1)*(cnt+1))+" links on "
								+java.time.LocalTime.now());
					}
				}
			} else {
				//"horizontally", no-linking
				for (int time = 0; time < numberOfTimepoints; ++time) {
					for (int cnt = 0; cnt < numberOfSpotsPerTimepoint; ++cnt) {
						//create and add a spot
						coords[0] = 100.0 * Math.cos(6.28 * (double)cnt / (double)numberOfSpotsPerTimepoint);
						coords[1] = 100.0 * Math.sin(6.28 * (double)cnt / (double)numberOfSpotsPerTimepoint);
						projectModel.getModel().getGraph().addVertex(auxSpot).init(time, coords, 0.87);
					}
					if (time > reportFromRound && (time % reportEveryNthRound) == 0) {
						System.out.println("added in total "+(numberOfSpotsPerTimepoint*(time+1))
								+" spots and on "+java.time.LocalTime.now());
					}
				}
			}

		} catch (Exception e) {
			System.out.println("GENERATOR ERROR: "+e.getMessage());
		} finally {
			System.out.println("GENERATOR FINISHED on "+java.time.LocalTime.now());

			//projectModel.getModel().getGraph().releaseRef(auxEdge);
			projectModel.getModel().getGraph().releaseRef(auxSpot);
			projectModel.getModel().getGraph().releaseRef(prevSpot);

			lock.writeLock().unlock();
			new ModelGraphListeners().resumeListeners();
			projectModel.getModel().setUndoPoint();
			projectModel.getModel().getGraph().notifyGraphChanged();
		}
	}

	class ModelGraphListeners extends ModelGraph {
		public void pauseListeners() {
			super.pauseListeners();
		}
		public void resumeListeners() {
			super.resumeListeners();
		}
	}
}

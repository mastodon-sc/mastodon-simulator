/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2023, Vladimír Ulman
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mastodon.mamut.spots;

import org.mastodon.mamut.ProjectModel;
import org.mastodon.mamut.model.ModelGraph;
import org.mastodon.mamut.model.Spot;
import org.mastodon.mamut.spots.util.CopyTagsBetweenSpots;

import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

@Plugin( type = Command.class, name = "Place spots on a surface" )
public class PlaceSpotsInSpotVolume implements Command {

	@Parameter(visibility = ItemVisibility.MESSAGE)
	private final String selectionInfoMsg = "At least one spot must be selected.";

	@Parameter(visibility = ItemVisibility.MESSAGE)
	private final String msg1 = "The created spots take the name from their source spot.";

	@Parameter(label = "Number of the created spots:")
	int targetCount = 10;

	@Parameter(label = "Radius of the created spots:")
	double targetRadius = 5.0;

	@Parameter(label = "Randomize radius (disabled=0):")
	double targetRadiusVar = 1.0;

	@Parameter(label = "Random distribution:")
	double targetRandomMove = 2.0;

	@Parameter(label = "Created spots are selected:")
	boolean shouldBeSelected = true;

	@Parameter(persist = false)
	ProjectModel projectModel;

	@Parameter
	LogService logService;

	@Override
	public void run() {
		//if nothing is selected, complain
		if (projectModel.getSelectionModel().isEmpty()) {
			logService.warn("Please, select at least one spot first!");
			return;
		}

		final CopyTagsBetweenSpots tagsUtil = new CopyTagsBetweenSpots(projectModel);
		final ModelGraph graph = projectModel.getModel().getGraph();

		final Spot newSpot = graph.vertexRef();
		for (Spot s : projectModel.getSelectionModel().getSelectedVertices()) {
			//for each Spot-volume 's':

			//the plan:
			//let's first generate a "full house" situation,
			//and then randomly kick out spots to reach the wanted number;
			//and then randomly shuffle these...

			final double minVolumeRadius = 0.0 +targetRadius +targetRandomMove;
			//wasn't permitting to get to the surface of the volume sphere:
			//final double maxVolumeRadius = Math.sqrt(s.getBoundingSphereRadiusSquared()) -targetRadius -targetRandomMove;
			final double maxVolumeRadius = Math.sqrt(s.getBoundingSphereRadiusSquared()) -targetRandomMove;
			//space required between two sphere centres:
			final double stepping = targetRadius + targetRandomMove + targetRandomMove + targetRadius;

			List<RealPoint> allPoints = new ArrayList<>(10000);
			for (double R = minVolumeRadius; R < maxVolumeRadius; R += stepping) {
				allPoints.addAll( enumerateSurfacePositions(s, R, stepping) );
			}

			if (allPoints.size() < targetCount) {
				logService.warn("Couldn't fit the requested "+targetCount+" new spheres into the volume '"+s.getLabel()+
					"' as I can fit in there only "+allPoints.size()+" spheres, which I'm keeping for now...");
			}

			//kick out until the correct size
			final Random rng = new Random();
			while (allPoints.size() > targetCount) {
				allPoints.remove( (int)Math.floor(rng.nextDouble()*allPoints.size()) );
			}

			//now randomly shuffle while inserting the RealPoints into Mastodon
			final double radiusSigma = 0.33 * targetRadiusVar;
			allPoints.forEach(p -> {
					final double[] coords = p.positionAsDoubleArray();
					coords[0] += rng.nextGaussian() * 0.33*targetRandomMove; //so that the whole range is <-targetRandomMove;+targetRandomMove>
					coords[1] += rng.nextGaussian() * 0.33*targetRandomMove;
					coords[2] += rng.nextGaussian() * 0.33*targetRandomMove;

					graph.addVertex(newSpot).init(s.getTimepoint(), coords, targetRadius + rng.nextGaussian()*radiusSigma);
					newSpot.setLabel( s.getLabel() );
					tagsUtil.insertSpotIntoSameTSAs(newSpot, s);
					if (shouldBeSelected) projectModel.getSelectionModel().setSelected(newSpot, true);
			});
		}
		graph.releaseRef(newSpot);
	}



	public static List<RealPoint> enumerateSurfacePositions(final RealLocalizable srcCentre,
	                                                        final double srcRadius,
	                                                        final double stepping) {
		List<RealPoint> list = new ArrayList<>(1000);
		final double quarterOfPerimeter = 0.5 * Math.PI * srcRadius;

		int steps = (int)Math.floor((4.0 * quarterOfPerimeter) / stepping);
		double stepsAng = 2.0 * Math.PI / (double)steps;
		for (int s = 0; s < steps; ++s) {
			list.add(new RealPoint(
					  srcCentre.getDoublePosition(0) + srcRadius * Math.cos((double)s * stepsAng),
					  srcCentre.getDoublePosition(1) + srcRadius * Math.sin((double)s * stepsAng),
					  srcCentre.getDoublePosition(2)  ));
		}

		int latLines = (int)Math.floor( quarterOfPerimeter / stepping );
		double latSteppingAng = 0.5 * Math.PI / (double)latLines;
		for (int l = 1; l < latLines; ++l) {
			double latRadius = srcRadius * Math.cos((double)l * latSteppingAng);
			double latPerimeter = 2.0 * Math.PI * latRadius;

			steps = (int)Math.floor(latPerimeter / stepping);
			stepsAng = 2.0 * Math.PI / (double)steps;

			for (int s = 0; s < steps; ++s) {
				list.add(new RealPoint(
						  srcCentre.getDoublePosition(0) + latRadius * Math.cos((double)s * stepsAng),
						  srcCentre.getDoublePosition(1) + latRadius * Math.sin((double)s * stepsAng),
						  srcCentre.getDoublePosition(2) + srcRadius * Math.sin((double)l * latSteppingAng) ));
				list.add(new RealPoint(
						  srcCentre.getDoublePosition(0) + latRadius * Math.cos((double)s * stepsAng),
						  srcCentre.getDoublePosition(1) + latRadius * Math.sin((double)s * stepsAng),
						  srcCentre.getDoublePosition(2) - srcRadius * Math.sin((double)l * latSteppingAng) ));
			}
		}

		return list;
	}
}

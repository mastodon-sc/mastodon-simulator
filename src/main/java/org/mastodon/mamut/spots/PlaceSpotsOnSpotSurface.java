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
public class PlaceSpotsOnSpotSurface implements Command {

	@Parameter(visibility = ItemVisibility.MESSAGE)
	private final String selectionInfoMsg = "At least one spot must be selected.";

	@Parameter(visibility = ItemVisibility.MESSAGE)
	private final String msg1 = "The created spots take the name from their source spot.";
	@Parameter(visibility = ItemVisibility.MESSAGE)
	private final String msg2 = "For simulator: Consider naming the source spot as keep_out.";

	@Parameter(label = "Radius of the created spots:")
	double targetRadius = 5.0;

	@Parameter(label = "Randomize radius (disabled=0):")
	double targetRadiusVar = 0.0;

	@Parameter(label = "Overlap of the created spots:")
	double targetOverlap = 1.5;

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

		final Random rng = new Random();
		final double radiusSigma = 0.33 * targetRadiusVar;
		final Spot newSpot = graph.vertexRef();
		for (Spot s : projectModel.getSelectionModel().getSelectedVertices()) {
			enumerateSurfacePositions(s, Math.sqrt(s.getBoundingSphereRadiusSquared()), targetRadius-targetOverlap)
					  .forEach(p -> {
						  graph.addVertex(newSpot).init(s.getTimepoint(), p.positionAsDoubleArray(), targetRadius + rng.nextGaussian()*radiusSigma);
						  newSpot.setLabel( s.getLabel() );
						  tagsUtil.insertSpotIntoSameTSAs(newSpot, s);
						  if (shouldBeSelected) projectModel.getSelectionModel().setSelected(newSpot, true);
					  });
		}
		graph.releaseRef(newSpot);
	}

	public static List<RealPoint> enumerateSurfacePositions(final RealLocalizable srcCentre,
	                                                        final double srcRadius,
	                                                        final double tgtRadius) {
		List<RealPoint> list = new ArrayList<>(1000);
		final double quarterOfPerimeter = 0.5 * Math.PI * srcRadius;

		int steps = (int)Math.floor((4.0 * quarterOfPerimeter) / (2.0 * tgtRadius));
		double stepsAng = 2.0 * Math.PI / (double)steps;
		for (int s = 0; s < steps; ++s) {
			list.add(new RealPoint(
					  srcCentre.getDoublePosition(0) + srcRadius * Math.cos((double)s * stepsAng),
					  srcCentre.getDoublePosition(1) + srcRadius * Math.sin((double)s * stepsAng),
					  srcCentre.getDoublePosition(2)  ));
		}

		int latLines = (int)Math.floor( quarterOfPerimeter / (2.0 * tgtRadius) );
		double latSteppingAng = 0.5 * Math.PI / (double)latLines;
		for (int l = 1; l < latLines; ++l) {
			double latRadius = srcRadius * Math.cos(l * latSteppingAng);
			double latPerimeter = 2.0 * Math.PI * latRadius;

			steps = (int)Math.floor(latPerimeter / (2.0 * tgtRadius));
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

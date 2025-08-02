package org.mastodon.mamut.spots;

import org.mastodon.mamut.ProjectModel;
import org.mastodon.mamut.model.ModelGraph;
import org.mastodon.mamut.model.Spot;
import org.mastodon.mamut.spots.util.CopyTagsBetweenSpots;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.log.Logger;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.mastodon.mamut.util.NumberSequenceHandler;

@Plugin( type = Command.class, name = "Duplicate spots in time" )
public class DuplicateSpots implements Command {

	@Parameter(visibility = ItemVisibility.MESSAGE)
	private final String selectionInfoMsg = "Duplicate all spots if none is selected, or only the selected ones.";

	@Parameter(label = "Select spots from this time point:")
	int sourceTimePoint = 0;

	@Parameter(label = "Link the added spots with the source Spot:")
	boolean doLinkSource = true;

	@Parameter(label = "Link the added spots themselves:")
	boolean doLink = true;

	@Parameter(label = "Target time points as, e.g., 1,2,5-8,10:")
	String targetTPsSpecification = "0";
	final SortedSet<Integer> timePoints = new TreeSet<>(); //to be filled in run() from the String above

	@Parameter(persist = false)
	ProjectModel appModel;

	@Parameter
	LogService logService;

	@Override
	public void run() {
		final Logger log = logService.subLogger("DuplicateMastodonPoints");

		graph = appModel.getModel().getGraph();
		prevSpot = graph.vertexRef();
		newSpot = graph.vertexRef();

		try {
			NumberSequenceHandler.parseSequenceOfNumbers(targetTPsSpecification, timePoints);
		} catch (ParseException e) {
			logService.error("Don't understand the target time points: "+e.getMessage());
			return;
		}

		tagsUtil = new CopyTagsBetweenSpots(appModel);

		final ReentrantReadWriteLock lock = graph.getLock();
		lock.writeLock().lock();
		try {
			if (appModel.getSelectionModel().isEmpty()) {
				processSpots((o) -> appModel.getModel().getSpatioTemporalIndex().getSpatialIndex(sourceTimePoint).iterator(), log);
			} else {
				processSpots((o) -> appModel.getSelectionModel().getSelectedVertices().iterator(), log);
			}
		} finally {
			lock.writeLock().unlock();
		}

		graph.releaseRef(prevSpot);
		graph.releaseRef(newSpot);

		log.info("Duplicating done.");
		appModel.getModel().getGraph().notifyGraphChanged();
	}

	ModelGraph graph;
	Spot prevSpot, newSpot;
	final double[] pos = new double[3];
	final double[][] cov = new double[3][3];
	CopyTagsBetweenSpots tagsUtil;

	public void processSpots(final Function<?,Iterator<Spot>> iterFactory, final Logger log) {
		final Set<Integer> toBeProcessedIDs = new HashSet<>(100000);
		iterFactory.apply(null).forEachRemaining( s -> {
			if (s.getTimepoint() == sourceTimePoint) toBeProcessedIDs.add(s.getInternalPoolIndex());
		} );

		while (!toBeProcessedIDs.isEmpty()) {
			log.info("There are "+toBeProcessedIDs.size()+" spots to be cloned...");
			Iterator<Spot> iter = iterFactory.apply(null);
			while (iter.hasNext()) {
				Spot s = iter.next();
				final int sId = s.getInternalPoolIndex();
				if (!toBeProcessedIDs.contains( sId )) continue;

				//add the past vertices first
				boolean firstAddition = true;
				for (int t : timePoints) { //assuming 't's are provided sorted
					if (t >= s.getTimepoint()) break;
					cloneToTime(s,t, !firstAddition && doLink);
					firstAddition = false;
				}
				if (!firstAddition && doLinkSource) graph.addEdge(prevSpot, s).init();

				//now add the future vertices
				prevSpot.refTo(s);
				firstAddition = true;
				for (int t : timePoints) { //assuming 't's are provided sorted
					if (t <= s.getTimepoint()) continue;
					cloneToTime(s,t, (firstAddition && doLinkSource) || (!firstAddition && doLink));
					firstAddition = false;
				}

				toBeProcessedIDs.remove( sId );
			}
			log.info("There are "+toBeProcessedIDs.size()+" spots left untouched...");
		}
	}

	private boolean cloneToTime(final Spot s, final int t, final boolean linkWithPrevSpot) {
		if (t < appModel.getMinTimepoint() || t > appModel.getMaxTimepoint()) return false;

		s.localize(pos);
		s.getCovariance(cov);
		graph.addVertex(newSpot).init(t, pos, cov);
		newSpot.setLabel(s.getLabel());
		tagsUtil.insertSpotIntoSameTSAs(newSpot, s);

		if (linkWithPrevSpot) graph.addEdge(prevSpot, newSpot).init();
		prevSpot.refTo(newSpot);

		return true;
	}
}

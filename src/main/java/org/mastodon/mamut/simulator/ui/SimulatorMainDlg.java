package org.mastodon.mamut.simulator.ui;

import net.imagej.ImageJ;
import org.mastodon.mamut.MainWindow;
import org.mastodon.mamut.ProjectModel;
import org.mastodon.mamut.io.project.MamutProject;
import org.mastodon.mamut.model.Model;
import org.mastodon.views.bdv.SharedBigDataViewerData;
import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.prefs.PrefService;
import org.mastodon.mamut.simulator.Simulator;
import org.mastodon.mamut.simulator.Agent2dMovesRestriction;
import org.mastodon.mamut.util.NumberSequenceHandler;
import javax.swing.WindowConstants;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Plugin(type = Command.class)
public class SimulatorMainDlg implements Command {
	@Parameter
	ProjectModel projectModel;

	@Parameter(visibility = ItemVisibility.MESSAGE)
	final String sep1 = "----------- Input -----------";

	@Parameter(label = "Start simulation:",
		choices = {"From scratch (seed new agents)",
		           "From existing spots",
		           "From the last populated time point"})
	String initMode = "From scratch";

	@Parameter(label = "Number of seed agents:", min="1",
	           description = "Used only if \"From scratch\" is selected above.")
	short numCells = 2;

	@Parameter(label = "Use existing spots from time point:", min="0",
	           description = "Used only if \"From existing spots\" is selected above.")
	int existingSpotsAtTP = 0;

	@Parameter(label = "Total number of time points to simulate:", min="1",
	           description = "Defines simulation duration.")
	int numTimepoints = 10;

	@Parameter(visibility = ItemVisibility.MESSAGE)
	final String sep2 = "----------- Output -----------";

	@Parameter(label = "Save snapshot at time points:", min="0",
	           description = "Comma-separated e.g. \"10,50,100\", or put \"don't save\".")
	String snapShotsTPs = "don't save";

	@Parameter(label = "Save snapshot files with base name:",
	           description = "Each snapshot will be saved as a separate .mastodon file with this base name + time point.")
	String snapShotsPath = "/temp/snapshots.mastodon";

	@Parameter(visibility = ItemVisibility.MESSAGE)
	final String sep3 = "----------- Parameters -----------";

	@Parameter(label = "Simulation dimensionality:",
	           choices = {"Full 3D",
	                      "Restricted to XY plane",
	                      "Restricted to XZ plane",
	                      "Restricted to YZ plane"} )
	String do2D = "Full 3D";

	@Parameter(label = "Show advanced settings:")
	boolean showAdvancedDlg = false;

	@Parameter(label = "Show progress bar:")
	boolean showProgressBar = true;

	@Parameter
	PrefService prefService;

	@Parameter
	LogService logService;

	@Parameter
	CommandService commandService;

	@Override
	public void run() {
		if (showAdvancedDlg) {
			//the advanced dialog will set some more params and will come back to this.runInsideMastodon()
			commandService.run(SimulatorAdvancedDlg.class,true,
					  "basicDialog",this,
					  "LABELS_NAMING_POLICY", "Lineage"); //to make sure that item will not show itself
			return;
		} else {
			//retrieve (and set) the params that the advanced dialog would have set...
			Simulator.setParamsFromConfig( SimulatorAdvancedDlg.loadSimConfigFromPrefStore(prefService) );
		}

		runInsideMastodon();
	}

	public void runInsideMastodon() {
		if (do2D.contains("XY")) Simulator.AGENT_DO_2D_MOVES_ONLY = Agent2dMovesRestriction.NO_Z_AXIS_MOVE;
		else if (do2D.contains("XZ")) Simulator.AGENT_DO_2D_MOVES_ONLY = Agent2dMovesRestriction.NO_Y_AXIS_MOVE;
		else if (do2D.contains("YZ")) Simulator.AGENT_DO_2D_MOVES_ONLY = Agent2dMovesRestriction.NO_X_AXIS_MOVE;
		else Simulator.AGENT_DO_2D_MOVES_ONLY = Agent2dMovesRestriction.NO_RESTRICTION;

		Runner r;
		if (!initMode.startsWith("From scratch")) {
			//re-use existing spots
			if (initMode.contains("the last")) existingSpotsAtTP = -1;
			r = new Runner(projectModel, existingSpotsAtTP, numTimepoints);
		} else {
			//insert own seeds
			r = new Runner(projectModel, numCells, numTimepoints);
		}

		//resolve snapshots before the simulation starts...
		final Set<Integer> ssTimepoints = new HashSet<>(20);
		try {
			if (snapShotsPath != null && !snapShotsPath.isEmpty()) {
				Path ssp = Paths.get(snapShotsPath);
				if (Files.isDirectory(ssp)) ssp = ssp.resolve("snapshots.mastodon");
				snapShotsPath = ssp.toAbsolutePath().toString();
				if (!snapShotsPath.endsWith(".mastodon")) snapShotsPath = snapShotsPath + ".mastodon";
				logService.info("Snapshots file template: " + snapShotsPath);

				ssTimepoints.addAll( NumberSequenceHandler.toSet(snapShotsTPs) );

				//if all went well, tell Runner about the snapshots
				r.setSnapshots(snapShotsPath, ssTimepoints);
			}
		} catch (RuntimeException e) {
			logService.info("Issue extracting snapshot time points: "+e.getMessage());
			logService.info("Managed to extract and thus will use : "+ssTimepoints);
		}

		r.setUseProgressBar(showProgressBar);
		r.run();
	}

	// ===============================================================================================
	public static void main(String[] args) {
		runWithGUI();
		//runHeadless("/temp/CLsim.mastodon");
	}

	public static void runWithGUI() {
		System.setProperty( "apple.laf.useScreenMenuBar", "true" );

		ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ProjectModel projectModel = ProjectModel.create(ij.getContext(),
				new Model(),
				SharedBigDataViewerData.fromDummyFilename("DUMMY x=100 y=100 z=100 t=1000.dummy"),
				new MamutProject("/temp/CLsim.mastodon"));
		final MainWindow win = new MainWindow( projectModel );
		win.setVisible( true );
		win.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );

		//and the GUI triggers the Simulator itself...
	}

	public static void runHeadless(final String projectFileName) {
		new Runner(projectFileName,(short)5,10).run();
	}
}

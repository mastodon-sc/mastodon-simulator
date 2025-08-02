package org.mastodon.mamut.spots;

import net.imglib2.RealPoint;
import org.joml.Vector3f;
import org.mastodon.mamut.ProjectModel;
import org.mastodon.mamut.spots.util.SpotsRotator;
import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.log.Logger;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin( type = Command.class, name = "Simpler-rotate spots in space" )
public class RotateSpotsInPlane implements Command {

	@Parameter(visibility = ItemVisibility.MESSAGE)
	private final String selectionInfoMsg = "Rotate all spots if none is selected, or only the selected ones.";

	@Parameter(label = "Coordinate system at time point: ",
			callback = "checkTimePoints")
	int sourceTime;

	@Parameter(label = "Label of the centre spot: ")
	String sourceCentre;

	@Parameter(label = "Label of the \"from\" spot: ")
	String sourceFrom;

	@Parameter(label = "Label of the \"to\" spot: ")
	String sourceTo;

	private void checkTimePoints() {
		sourceTime = Math.max(appModel.getMinTimepoint(), Math.min(sourceTime, appModel.getMaxTimepoint()) );
	}

	@Parameter(label = "If no spots are selected, which time points to process:",
			description = "Accepted format is comma-separated number or interval: A-B,C,D,E-F,G",
			required = false)
	String chosenTimepoints = "0,1-5,6,7-9";

	@Parameter(persist = false)
	ProjectModel appModel;

	@Parameter
	LogService logService;

	@Override
	public void run() {
		final Logger log = logService.subLogger("ShiftAndRotateMastodonPoints");
		try {
			final SpotsRotator.InitialSetting setting = new SpotsRotator.InitialSetting();
			setting.appModel = this.appModel;
			setting.sC = RotateSpotsGeneral.getCoordinateFromMastodonSpot(appModel, sourceCentre, sourceTime);
			setting.sR = RotateSpotsGeneral.getCoordinateFromMastodonSpot(appModel, sourceFrom, sourceTime);
			setting.tR = RotateSpotsGeneral.getCoordinateFromMastodonSpot(appModel, sourceTo, sourceTime);
			log.info("Found all three spots, good.");

			final float[] coord = new float[3];
			setting.sC.localize(coord);
			Vector3f centre = new Vector3f(coord);
			setting.sR.localize(coord);
			Vector3f vecA = (new Vector3f(coord)).sub(centre);
			setting.tR.localize(coord);
			Vector3f vecB = (new Vector3f(coord)).sub(centre);
			vecA.cross(vecB).normalize(100.f);
			centre.add(vecA);
			setting.sU = new RealPoint(centre.x, centre.y, centre.z);

			setting.tC = setting.sC;
			setting.tU = setting.sU;

			RotateSpotsGeneral.execute(setting, chosenTimepoints, log);
		} catch (IllegalArgumentException e) {
			log.error("Error running the plugin: " + e.getMessage());
		}
	}
}

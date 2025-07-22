package org.mastodon.mamut;

import net.imagej.patcher.LegacyInjector;
import net.imagej.ImageJ;
import org.mastodon.mamut.io.ProjectLoader;
import mpicbg.spim.data.SpimDataException;

import java.io.IOException;

public class StartMastodon {
	public static void main(String[] args) {
		LegacyInjector.preinit();

		try {
			ImageJ ij = new ImageJ();
			ij.ui().showUI();
			//GUI must be there, otherwise dialogs won't open

			ProjectModel p = ProjectLoader.open(
					  "/temp/PLAY_WITH_ME.mastodon",
					  ij.context(),
					  true,
					  true
			);
		} catch (IOException | SpimDataException e) {
			throw new RuntimeException(e);
		}
	}
}

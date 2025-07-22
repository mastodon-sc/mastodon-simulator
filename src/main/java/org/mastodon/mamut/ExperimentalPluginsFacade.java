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
package org.mastodon.mamut;

import static org.mastodon.app.ui.ViewMenuBuilder.item;
import static org.mastodon.app.ui.ViewMenuBuilder.menu;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.mastodon.app.ui.ViewMenuBuilder;
import org.mastodon.mamut.spots.RotateSpotsGeneral;
import org.mastodon.mamut.spots.RotateSpotsInPlane;
import org.mastodon.mamut.spots.ShiftSpots;
import org.mastodon.mamut.spots.DuplicateSpots;
import org.mastodon.mamut.trees.LineageRandomColorizer;
import org.mastodon.mamut.plugin.MamutPlugin;
import org.scijava.ui.behaviour.io.gui.CommandDescriptionProvider;
import org.scijava.ui.behaviour.io.gui.CommandDescriptions;
import org.mastodon.ui.keymap.KeyConfigContexts;

import org.scijava.AbstractContextual;
import org.scijava.command.CommandService;
import org.scijava.plugin.Plugin;
import org.scijava.ui.behaviour.util.Actions;
import org.scijava.ui.behaviour.util.AbstractNamedAction;
import org.scijava.ui.behaviour.util.RunnableAction;
import org.mastodon.mamut.simulator.ui.SimulatorMainDlg;
import org.mastodon.mamut.spots.PlaceSpotsOnSpotSurface;
import org.mastodon.mamut.spots.PlaceSpotsInSpotVolume;

@Plugin( type = MamutPlugin.class )
public class ExperimentalPluginsFacade extends AbstractContextual implements MamutPlugin
{
	//"IDs" of all plug-ins wrapped in this class
	private static final String EXP_SHIFTSPOTS = "[msim] shift spots";
	private static final String EXP_DUPLICATESPOTS = "[msim] duplicate spots";
	private static final String EXP_PLANEROTATESPOTS = "[msim] in plane rotate spots";
	private static final String EXP_GENROTATESPOTS = "[msim] general rotate spots";
	private static final String EXP_LINEAGECOLORIZER = "[msim] random color tags";
	private static final String EXP_SURFACESPOTS = "[msim] place surface spots";
	private static final String EXP_VOLUMESPOTS = "[msim] place volume spots";
	private static final String EXP_SIMULATOR = "[msim] simulator";

	private static final String[] EXP_SHIFTSPOTS_KEYS = { "not mapped" };
	private static final String[] EXP_DUPLICATESPOTS_KEYS = { "not mapped" };
	private static final String[] EXP_PLANEROTATESPOTS_KEYS = { "not mapped" };
	private static final String[] EXP_GENROTATESPOTS_KEYS = { "not mapped" };
	private static final String[] EXP_LINEAGECOLORIZER_KEYS = { "not mapped" };
	private static final String[] EXP_SURFACESPOTS_KEYS = { "not mapped" };
	private static final String[] EXP_VOLUMESPOTS_KEYS = { "not mapped" };
	private static final String[] EXP_SIMULATOR_KEYS = { "not mapped" };
	//------------------------------------------------------------------------

	/** titles of this plug-in's menu items */
	private static final Map< String, String > menuTexts = new HashMap<>();
	static
	{
		menuTexts.put( EXP_SHIFTSPOTS, "Translate spots in space and time" );
		menuTexts.put( EXP_DUPLICATESPOTS, "Duplicate spots" );
		menuTexts.put( EXP_PLANEROTATESPOTS, "Rotate spots in 2D" );
		menuTexts.put( EXP_GENROTATESPOTS, "Rotate spots in 3D" );
		menuTexts.put( EXP_SURFACESPOTS, "Add spots on selected spot surface" );
		menuTexts.put( EXP_VOLUMESPOTS, "Add spots into selected spot interior" );
		menuTexts.put( EXP_LINEAGECOLORIZER, "Add or use tag set to highlight lineage trees" );
		menuTexts.put( EXP_SIMULATOR, "Simulator" );
	}
	@Override
	public Map< String, String > getMenuTexts() { return menuTexts; }

	@Override
	public List< ViewMenuBuilder.MenuItem > getMenuItems()
	{
		return Collections.singletonList(
			menu( "Plugins",
				menu( "Tags",
					item( EXP_LINEAGECOLORIZER )
				),
				menu( "Spots management",
					menu( "Spots shuffling (Simulator)",
						item( EXP_SHIFTSPOTS ),
						item( EXP_DUPLICATESPOTS ),
						item( EXP_PLANEROTATESPOTS ),
						item( EXP_GENROTATESPOTS ),
						item( EXP_SURFACESPOTS ),
						item( EXP_VOLUMESPOTS )
					)
				),
				item( EXP_SIMULATOR )
			)
		);
	}

	/** Command descriptions for all provided commands */
	@Plugin( type = Descriptions.class )
	public static class Descriptions extends CommandDescriptionProvider
	{
		public Descriptions()
		{
			super( KeyConfigScopes.MAMUT, KeyConfigContexts.TRACKSCHEME, KeyConfigContexts.BIGDATAVIEWER );
		}

		@Override
		public void getCommandDescriptions( final CommandDescriptions descriptions )
		{
			descriptions.add(EXP_SHIFTSPOTS, EXP_SHIFTSPOTS_KEYS, "Scale spots and translate them by a defined offset in both spatial and temporal domains.");
			descriptions.add(EXP_DUPLICATESPOTS, EXP_DUPLICATESPOTS_KEYS, "Create identical copies of selected or specified spots across multiple time points. Clone spots into multiple other time points.");
			descriptions.add(EXP_PLANEROTATESPOTS, EXP_PLANEROTATESPOTS_KEYS, "Rotate spots in spatial domain around chosen axis, resulting in a 2D rotation.");
			descriptions.add(EXP_GENROTATESPOTS, EXP_GENROTATESPOTS_KEYS, "Apply a general 3D spatial rotation to selected spots.");
			descriptions.add(EXP_SURFACESPOTS, EXP_SURFACESPOTS_KEYS, "Generate a surface layer of spots around a selected spot.");
			descriptions.add(EXP_VOLUMESPOTS, EXP_VOLUMESPOTS_KEYS, "Fill a 3D volume of a selected spot with regularly distributed spots.");
			descriptions.add(EXP_LINEAGECOLORIZER, EXP_LINEAGECOLORIZER_KEYS, "Assign to every lineage tree a randomly chosen color from the selected tag set.");
			descriptions.add(EXP_SIMULATOR, EXP_SIMULATOR_KEYS, "Generates a new random cell lineage.");
		}
	}
	//------------------------------------------------------------------------


	private final AbstractNamedAction actionShiftSpots;
	private final AbstractNamedAction actionDuplicateSpots;
	private final AbstractNamedAction actionPlaneRotateSpots;
	private final AbstractNamedAction actionGenRotateSpots;
	private final AbstractNamedAction actionLineageColorizer;
	private final AbstractNamedAction actionSurfaceSpots;
	private final AbstractNamedAction actionVolumeSpots;
	private final AbstractNamedAction actionSimulator;

	/** reference to the currently available project in Mastodon */
	private ProjectModel pluginAppModel;

	/** default c'tor: creates Actions available from this plug-in */
	public ExperimentalPluginsFacade()
	{
		actionShiftSpots = new RunnableAction(EXP_SHIFTSPOTS, this::shiftSpots);
		actionDuplicateSpots = new RunnableAction(EXP_DUPLICATESPOTS, this::duplicateSpots);
		actionPlaneRotateSpots = new RunnableAction(EXP_PLANEROTATESPOTS, this::rotateSpotsInPlane);
		actionGenRotateSpots = new RunnableAction(EXP_GENROTATESPOTS, this::rotateSpotsGeneral);
		actionLineageColorizer = new RunnableAction(EXP_LINEAGECOLORIZER, this::lineageColorizer);
		actionSurfaceSpots = new RunnableAction(EXP_SURFACESPOTS, this::spotsOnSurface);
		actionVolumeSpots = new RunnableAction(EXP_VOLUMESPOTS, this::spotsInVolume);
		actionSimulator = new RunnableAction(EXP_SIMULATOR, this::simulator);
		updateEnabledActions();
	}

	/** register the actions to the application (with no shortcut keys) */
	@Override
	public void installGlobalActions( final Actions actions )
	{
		actions.namedAction(actionShiftSpots, EXP_SHIFTSPOTS_KEYS);
		actions.namedAction(actionDuplicateSpots, EXP_DUPLICATESPOTS_KEYS);
		actions.namedAction(actionPlaneRotateSpots, EXP_PLANEROTATESPOTS_KEYS);
		actions.namedAction(actionGenRotateSpots, EXP_GENROTATESPOTS_KEYS);
		actions.namedAction(actionLineageColorizer, EXP_LINEAGECOLORIZER_KEYS);
		actions.namedAction(actionSurfaceSpots, EXP_SURFACESPOTS_KEYS);
		actions.namedAction(actionVolumeSpots, EXP_VOLUMESPOTS_KEYS);
		actions.namedAction(actionSimulator, EXP_SIMULATOR_KEYS);
	}

	/** learn about the current project's params */
	@Override
	public void setAppPluginModel( final ProjectModel model )
	{
		//the application reports back to us if some project is available
		this.pluginAppModel = model;
		updateEnabledActions();
	}

	/** enables/disables menu items based on the availability of some project */
	private void updateEnabledActions()
	{
		actionShiftSpots.setEnabled( pluginAppModel != null );
		actionDuplicateSpots.setEnabled( pluginAppModel != null );
		actionPlaneRotateSpots.setEnabled( pluginAppModel != null );
		actionGenRotateSpots.setEnabled( pluginAppModel != null );
		actionLineageColorizer.setEnabled( pluginAppModel != null );
		actionSurfaceSpots.setEnabled( pluginAppModel != null );
		actionVolumeSpots.setEnabled( pluginAppModel != null );
		actionSimulator.setEnabled( pluginAppModel != null );
	}
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	private void shiftSpots()
	{
		this.getContext().getService(CommandService.class).run(
			ShiftSpots.class, true,
			"appModel", pluginAppModel
		);
	}

	private void duplicateSpots()
	{
		this.getContext().getService(CommandService.class).run(
			DuplicateSpots.class, true,
			"appModel", pluginAppModel
		);
	}

	private void rotateSpotsInPlane()
	{
		this.getContext().getService(CommandService.class).run(
			RotateSpotsInPlane.class, true,
			"appModel", pluginAppModel
		);
	}

	private void rotateSpotsGeneral()
	{
		this.getContext().getService(CommandService.class).run(
			RotateSpotsGeneral.class, true,
			"appModel", pluginAppModel
		);
	}

	private void lineageColorizer()
	{
		this.getContext().getService(CommandService.class).run(
			LineageRandomColorizer.class, true,
			"pluginAppModel", pluginAppModel
		);
	}

	private void simulator()
	{
		this.getContext().getService(CommandService.class).run(
			SimulatorMainDlg.class, true,
			"projectModel", pluginAppModel
		);
	}

	private void spotsOnSurface() {
		this.getContext().getService(CommandService.class).run(
			PlaceSpotsOnSpotSurface.class, true,
			"projectModel", pluginAppModel
		);
	}

	private void spotsInVolume() {
		this.getContext().getService(CommandService.class).run(
			PlaceSpotsInSpotVolume.class, true,
			"projectModel", pluginAppModel
		);
	}
}

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
package org.mastodon.mamut.simulator.ui;

import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.prefs.PrefService;
import org.mastodon.mamut.simulator.Simulator;
import org.mastodon.mamut.simulator.AgentNamingPolicy;
import org.mastodon.mamut.simulator.SimulationConfig;

@Plugin(type = Command.class)
public class SimulatorAdvancedDlg implements Command {
	public static final String HEADER1 = "----------- Agent Spot Size -----------";
	public static final String HEADER2 = "----------- Agents Mobility Parameters -----------";
	public static final String HEADER3 = "----------- Agents Life-cycle Parameters -----------";
	public static final String HEADER4 = "----------- Offspring Agents Settings -----------";
	public static final String HEADER5 = "----------- Simulation Debug Options -----------";

	@Parameter(visibility = ItemVisibility.MESSAGE)
	final String sep1 = HEADER1;

	@Parameter(label = "Initial agent radius:",
	           description = "AGENT_INITIAL_RADIUS: Using this radius the new spots are introduced into the simulation.")
	double AGENT_INITIAL_RADIUS = Simulator.AGENT_INITIAL_RADIUS;

	@Parameter(visibility = ItemVisibility.MESSAGE)
	final String sep2 = HEADER2;

	@Parameter(label = "Look-around distance:",
	           description = "AGENT_LOOK_AROUND_DISTANCE: How far an agent \"sees\" to explore nearby space, to consider nearby agents positions for its own development.")
	double AGENT_LOOK_AROUND_DISTANCE = Simulator.AGENT_LOOK_AROUND_DISTANCE;

	@Parameter(label = "Min distance to other agents:",
	           description = "AGENT_MIN_DISTANCE_TO_ANOTHER_AGENT: How close two agents can come before they start repelling each other.")
	double AGENT_MIN_DISTANCE_TO_ANOTHER_AGENT = Simulator.AGENT_MIN_DISTANCE_TO_ANOTHER_AGENT;

	@Parameter(label = "Usual step size (movement distance):",
	           description = "AGENT_USUAL_STEP_SIZE: How far an agent can move between time points.")
	double AGENT_USUAL_STEP_SIZE = Simulator.AGENT_USUAL_STEP_SIZE;

	@Parameter(label = "Max attempts to make a move:",
	           description = "AGENT_NUMBER_OF_ATTEMPTS_TO_MAKE_A_MOVE: How many attempts is an agent (cell) allowed to try to move randomly until it finds a non-colliding position.")
	int AGENT_NUMBER_OF_ATTEMPTS_TO_MAKE_A_MOVE = Simulator.AGENT_NUMBER_OF_ATTEMPTS_TO_MAKE_A_MOVE;

	@Parameter(visibility = ItemVisibility.MESSAGE)
	final String sep3 = HEADER3;

	@Parameter(label = "Average lifespan before division:",
	           description = "AGENT_AVERAGE_LIFESPAN_BEFORE_DIVISION: The mean life span of an agent (cell). Shorter means divisions occurs more often. 15%-down-rounded of the lifespan is a period just prior a division when mother cell slows down.")
	int AGENT_AVERAGE_LIFESPAN_BEFORE_DIVISION = Simulator.AGENT_AVERAGE_LIFESPAN_BEFORE_DIVISION;

	@Parameter(label = "Max lifespan (dies after):",
	           description = "AGENT_MAX_LIFESPAN_AND_DIES_AFTER: Hard limit on the life span of an agent (cell). The cell dies, is removed from the simulation, whenever it's life exceeded this value.")
	int AGENT_MAX_LIFESPAN_AND_DIES_AFTER = Simulator.AGENT_MAX_LIFESPAN_AND_DIES_AFTER;

	@Parameter(label = "Max local density to allow division:",
	           description = "AGENT_MAX_DENSITY_TO_ENABLE_DIVISION: The maximum number of neighbors tolerated for a division to occur; if more neighbors are around, the system believes the space is too condensed and doesn't permit agents (cells) to divide.")
	int AGENT_MAX_DENSITY_TO_ENABLE_DIVISION = Simulator.AGENT_MAX_DENSITY_TO_ENABLE_DIVISION;

	@Parameter(label = "Max variability of division planes:",
	           description = "AGENT_MAX_VARIABILITY_OF_DIVISION_PLANES: Given the last division direction (dozering direction) of a mother cell, daughters will divide in a new, random division direction such that the angle between the two division directions is not more than this.")
	double AGENT_MAX_VARIABILITY_OF_DIVISION_PLANES = Simulator.AGENT_MAX_VARIABILITY_OF_DIVISION_PLANES;

	@Parameter(visibility = ItemVisibility.MESSAGE)
	final String sep4 = HEADER4;

	@Parameter(label = "Initial distance after division:",
	           description = "AGENT_DAUGHTERS_INITIAL_DISTANCE: Freshly \"born\" daughters are placed exactly this distance apart from one another.")
	double AGENT_DAUGHTERS_INITIAL_DISTANCE = Simulator.AGENT_DAUGHTERS_INITIAL_DISTANCE;

	@Parameter(label = "Dozering (repulsion) distance:",
	           description = "AGENT_DAUGHTERS_DOZERING_DISTANCE: Distance over which newly divided agents are pushed apart. Two daughters translate away from their INITIAL_DISTANCE to AGENT_DAUGHTERS_DOZERING_DISTANCE for this number of time points.")
	double AGENT_DAUGHTERS_DOZERING_DISTANCE = Simulator.AGENT_DAUGHTERS_DOZERING_DISTANCE;

	@Parameter(label = "Dozering time period:",
	           description = "AGENT_DAUGHTERS_DOZERING_TIME_PERIOD: Number of time points during which newly divided agents push apart; in this period the daughters are influenced only by surrounding-and-overlapping agents, but the surrounding agents are influenced by these daughters normally (so the influence is asymmetrical).")
	int AGENT_DAUGHTERS_DOZERING_TIME_PERIOD = Simulator.AGENT_DAUGHTERS_DOZERING_TIME_PERIOD;

	@Parameter(visibility = ItemVisibility.MESSAGE)
	final String sep5 = HEADER5;

	/* makes it not visible, yet settable from script or command line (not from GUI as Fiji won't show it)
	@Parameter(description = "Spots labels can be either 'M' or can be encoding the lineage history, also optionally with debug hints _B,_W,_BW.",
		choices = { "Lineage encoding labels (1aabba...)",
		            "Prepend hints B_,W_,BW_ to encoding labels",
		            "Append hints _B,_W,_BW to encoding labels",
		            "Always 'M' (no lineage encoding)" })
	*/
	@Parameter(required = false, visibility = ItemVisibility.INVISIBLE)
	String LABELS_NAMING_POLICY = "Lineage";

	@Parameter(label = "Collect internal data:",
			  description = "COLLECT_INTERNAL_DATA: Collect internal status info per every Agent. If not, may speed up the simulation as no extra data will be stored.")
	boolean COLLECT_INTERNAL_DATA = Simulator.COLLECT_INTERNAL_DATA;

	@Parameter(label = "Verbose debug outputs from agents:",
			  description = "VERBOSE_AGENT_DEBUG: Prints a lot of data to understand decisions making of the agents.")
	boolean VERBOSE_AGENT_DEBUG = Simulator.VERBOSE_AGENT_DEBUG;

	@Parameter(label = "Verbose debug outputs from simulator:",
			  description = "VERBOSE_SIMULATOR_DEBUG: Prints relative little reports about what the simulation framework was asked to do.")
	boolean VERBOSE_SIMULATOR_DEBUG = Simulator.VERBOSE_SIMULATOR_DEBUG;

	@Parameter(label = "Each time point, add centre spot of agents:",
			  description = "CREATE_MASTODON_CENTER_SPOT: Produce a \"lineage\" that stays in the geometric centre of the generated data.")
	boolean CREATE_MASTODON_CENTER_SPOT = Simulator.CREATE_MASTODON_CENTER_SPOT;

	@Parameter
	SimulatorMainDlg basicDialog = null;

	@Override
	public void run() {
		Simulator.LABELS_NAMING_POLICY =                     getAgentNamingPolicyFrom(this.LABELS_NAMING_POLICY);
		Simulator.COLLECT_INTERNAL_DATA =                    COLLECT_INTERNAL_DATA;
		Simulator.VERBOSE_AGENT_DEBUG =                      VERBOSE_AGENT_DEBUG;
		Simulator.VERBOSE_SIMULATOR_DEBUG =                  VERBOSE_SIMULATOR_DEBUG;
		Simulator.AGENT_LOOK_AROUND_DISTANCE =               AGENT_LOOK_AROUND_DISTANCE;
		Simulator.AGENT_MIN_DISTANCE_TO_ANOTHER_AGENT =      AGENT_MIN_DISTANCE_TO_ANOTHER_AGENT;
		Simulator.AGENT_USUAL_STEP_SIZE =                    AGENT_USUAL_STEP_SIZE;
		Simulator.AGENT_NUMBER_OF_ATTEMPTS_TO_MAKE_A_MOVE =  AGENT_NUMBER_OF_ATTEMPTS_TO_MAKE_A_MOVE;
		Simulator.AGENT_AVERAGE_LIFESPAN_BEFORE_DIVISION =   AGENT_AVERAGE_LIFESPAN_BEFORE_DIVISION;
		Simulator.AGENT_MAX_LIFESPAN_AND_DIES_AFTER =        AGENT_MAX_LIFESPAN_AND_DIES_AFTER;
		Simulator.AGENT_MAX_DENSITY_TO_ENABLE_DIVISION =     AGENT_MAX_DENSITY_TO_ENABLE_DIVISION;
		Simulator.AGENT_MAX_VARIABILITY_OF_DIVISION_PLANES = AGENT_MAX_VARIABILITY_OF_DIVISION_PLANES;
		Simulator.AGENT_DAUGHTERS_INITIAL_DISTANCE =         AGENT_DAUGHTERS_INITIAL_DISTANCE;
		Simulator.AGENT_DAUGHTERS_DOZERING_DISTANCE =        AGENT_DAUGHTERS_DOZERING_DISTANCE;
		Simulator.AGENT_DAUGHTERS_DOZERING_TIME_PERIOD =     AGENT_DAUGHTERS_DOZERING_TIME_PERIOD;
		Simulator.AGENT_INITIAL_RADIUS =                     AGENT_INITIAL_RADIUS;
		Simulator.CREATE_MASTODON_CENTER_SPOT =              CREATE_MASTODON_CENTER_SPOT;
		if (basicDialog != null) basicDialog.runInsideMastodon();
	}


	static AgentNamingPolicy getAgentNamingPolicyFrom(final String policyStr) {
		if (policyStr == null) return AgentNamingPolicy.ENCODING_LABELS;

		if (policyStr.startsWith("Always")) {
			return AgentNamingPolicy.USE_ALWAYS_M;
		} else if (policyStr.startsWith("Prepend")) {
			return AgentNamingPolicy.ENCODING_LABELS_AND_PREPENDING;
		} else if (policyStr.startsWith("Append")) {
			return AgentNamingPolicy.ENCODING_LABELS_AND_APPENDING;
		} else {
			return AgentNamingPolicy.ENCODING_LABELS;
		}
	}

	static SimulationConfig loadSimConfigFromPrefStore(final PrefService prefService) {
		final SimulationConfig cfg = new SimulationConfig();
		cfg.LABELS_NAMING_POLICY = getAgentNamingPolicyFrom( prefService.get(SimulatorAdvancedDlg.class, "LABELS_NAMING_POLICY", "encoding labels") );
		cfg.COLLECT_INTERNAL_DATA =                          prefService.getBoolean(SimulatorAdvancedDlg.class, "COLLECT_INTERNAL_DATA", Simulator.COLLECT_INTERNAL_DATA);
		cfg.VERBOSE_AGENT_DEBUG =                            prefService.getBoolean(SimulatorAdvancedDlg.class, "VERBOSE_AGENT_DEBUG", Simulator.VERBOSE_AGENT_DEBUG);
		cfg.VERBOSE_SIMULATOR_DEBUG =                        prefService.getBoolean(SimulatorAdvancedDlg.class, "VERBOSE_SIMULATOR_DEBUG", Simulator.VERBOSE_SIMULATOR_DEBUG);
		cfg.AGENT_LOOK_AROUND_DISTANCE =                     prefService.getDouble(SimulatorAdvancedDlg.class, "AGENT_LOOK_AROUND_DISTANCE", Simulator.AGENT_LOOK_AROUND_DISTANCE);
		cfg.AGENT_MIN_DISTANCE_TO_ANOTHER_AGENT =            prefService.getDouble(SimulatorAdvancedDlg.class, "AGENT_MIN_DISTANCE_TO_ANOTHER_AGENT", Simulator.AGENT_MIN_DISTANCE_TO_ANOTHER_AGENT);
		cfg.AGENT_USUAL_STEP_SIZE =                          prefService.getDouble(SimulatorAdvancedDlg.class, "AGENT_USUAL_STEP_SIZE", Simulator.AGENT_USUAL_STEP_SIZE);
		cfg.AGENT_NUMBER_OF_ATTEMPTS_TO_MAKE_A_MOVE =        prefService.getInt(SimulatorAdvancedDlg.class, "AGENT_NUMBER_OF_ATTEMPTS_TO_MAKE_A_MOVE", Simulator.AGENT_NUMBER_OF_ATTEMPTS_TO_MAKE_A_MOVE);
		cfg.AGENT_AVERAGE_LIFESPAN_BEFORE_DIVISION =         prefService.getInt(SimulatorAdvancedDlg.class, "AGENT_AVERAGE_LIFESPAN_BEFORE_DIVISION", Simulator.AGENT_AVERAGE_LIFESPAN_BEFORE_DIVISION);
		cfg.AGENT_MAX_LIFESPAN_AND_DIES_AFTER =              prefService.getInt(SimulatorAdvancedDlg.class, "AGENT_MAX_LIFESPAN_AND_DIES_AFTER", Simulator.AGENT_MAX_LIFESPAN_AND_DIES_AFTER);
		cfg.AGENT_MAX_DENSITY_TO_ENABLE_DIVISION =           prefService.getInt(SimulatorAdvancedDlg.class, "AGENT_MAX_DENSITY_TO_ENABLE_DIVISION", Simulator.AGENT_MAX_DENSITY_TO_ENABLE_DIVISION);
		cfg.AGENT_MAX_VARIABILITY_OF_DIVISION_PLANES =       prefService.getDouble(SimulatorAdvancedDlg.class, "AGENT_MAX_VARIABILITY_OF_DIVISION_PLANES", Simulator.AGENT_MAX_VARIABILITY_OF_DIVISION_PLANES);
		cfg.AGENT_DAUGHTERS_INITIAL_DISTANCE =               prefService.getDouble(SimulatorAdvancedDlg.class, "AGENT_DAUGHTERS_INITIAL_DISTANCE", Simulator.AGENT_DAUGHTERS_INITIAL_DISTANCE);
		cfg.AGENT_DAUGHTERS_DOZERING_DISTANCE =              prefService.getDouble(SimulatorAdvancedDlg.class, "AGENT_DAUGHTERS_DOZERING_DISTANCE", Simulator.AGENT_DAUGHTERS_DOZERING_DISTANCE);
		cfg.AGENT_DAUGHTERS_DOZERING_TIME_PERIOD =           prefService.getInt(SimulatorAdvancedDlg.class, "AGENT_DAUGHTERS_DOZERING_TIME_PERIOD", Simulator.AGENT_DAUGHTERS_DOZERING_TIME_PERIOD);
		cfg.AGENT_INITIAL_RADIUS =                           prefService.getDouble(SimulatorAdvancedDlg.class, "AGENT_INITIAL_RADIUS", Simulator.AGENT_INITIAL_RADIUS);
		cfg.CREATE_MASTODON_CENTER_SPOT =                    prefService.getBoolean(SimulatorAdvancedDlg.class, "CREATE_MASTODON_CENTER_SPOT", Simulator.CREATE_MASTODON_CENTER_SPOT);
		return cfg;
	}
}

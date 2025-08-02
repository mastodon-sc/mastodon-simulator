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
package org.mastodon.mamut.simulator;

public class SimulationConfig {
	public AgentNamingPolicy LABELS_NAMING_POLICY          = Simulator.LABELS_NAMING_POLICY;
	public boolean COLLECT_INTERNAL_DATA                   = Simulator.COLLECT_INTERNAL_DATA;
	public boolean VERBOSE_AGENT_DEBUG                     = Simulator.VERBOSE_AGENT_DEBUG;
	public boolean VERBOSE_SIMULATOR_DEBUG                 = Simulator.VERBOSE_SIMULATOR_DEBUG;
	public double AGENT_LOOK_AROUND_DISTANCE               = Simulator.AGENT_LOOK_AROUND_DISTANCE;
	public double AGENT_MIN_DISTANCE_TO_ANOTHER_AGENT      = Simulator.AGENT_MIN_DISTANCE_TO_ANOTHER_AGENT;
	public double AGENT_USUAL_STEP_SIZE                    = Simulator.AGENT_USUAL_STEP_SIZE;
	public int AGENT_NUMBER_OF_ATTEMPTS_TO_MAKE_A_MOVE     = Simulator.AGENT_NUMBER_OF_ATTEMPTS_TO_MAKE_A_MOVE;
	public Agent2dMovesRestriction AGENT_DO_2D_MOVES_ONLY  = Simulator.AGENT_DO_2D_MOVES_ONLY;
	public int AGENT_AVERAGE_LIFESPAN_BEFORE_DIVISION      = Simulator.AGENT_AVERAGE_LIFESPAN_BEFORE_DIVISION;
	public int AGENT_MAX_LIFESPAN_AND_DIES_AFTER           = Simulator.AGENT_MAX_LIFESPAN_AND_DIES_AFTER;
	public int AGENT_MAX_DENSITY_TO_ENABLE_DIVISION        = Simulator.AGENT_MAX_DENSITY_TO_ENABLE_DIVISION;
	public double AGENT_MAX_VARIABILITY_OF_DIVISION_PLANES = Simulator.AGENT_MAX_VARIABILITY_OF_DIVISION_PLANES;
	public double AGENT_DAUGHTERS_INITIAL_DISTANCE         = Simulator.AGENT_DAUGHTERS_INITIAL_DISTANCE;
	public double AGENT_DAUGHTERS_DOZERING_DISTANCE        = Simulator.AGENT_DAUGHTERS_DOZERING_DISTANCE;
	public int AGENT_DAUGHTERS_DOZERING_TIME_PERIOD        = Simulator.AGENT_DAUGHTERS_DOZERING_TIME_PERIOD;
	public double AGENT_INITIAL_RADIUS                     = Simulator.AGENT_INITIAL_RADIUS;
	public boolean CREATE_MASTODON_CENTER_SPOT             = Simulator.CREATE_MASTODON_CENTER_SPOT;
}

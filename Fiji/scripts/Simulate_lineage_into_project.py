#@Context ctx
#@File initial_mastodon_project_file

from org.mastodon.mamut import MainWindow
from org.mastodon.mamut.io import ProjectLoader
from org.mastodon.mamut.simulator.ui import Runner
from org.mastodon.mamut.simulator import SimulationConfig
from org.mastodon.mamut.simulator import AgentNamingPolicy
from org.mastodon.mamut.simulator import Agent2dMovesRestriction

# Loads the Mastodon project and shows the Mastodon app.
project_model = ProjectLoader.open(initial_mastodon_project_file.toString(), ctx, True, True)
MainWindow(project_model).setVisible(True)

# Possibly adjusts the simulation configuration from the default configuration.
# (This is an optional step.)
sim_cfg = SimulationConfig()

# --------------------- own configuration ---------------------
# For example:
# Spots labels can be either 'M', or can be encoding the lineage history, also optionally with debug hints _B,_W,_BW.
sim_cfg.LABELS_NAMING_POLICY = AgentNamingPolicy.USE_ALWAYS_M                   # Always 'M' (no lineage encoding)
sim_cfg.LABELS_NAMING_POLICY = AgentNamingPolicy.ENCODING_LABELS                # Lineage encoding labels (1aabba...)
sim_cfg.LABELS_NAMING_POLICY = AgentNamingPolicy.ENCODING_LABELS_AND_PREPENDING # Prepend hints B_,W_,BW_ to encoding labels
sim_cfg.LABELS_NAMING_POLICY = AgentNamingPolicy.ENCODING_LABELS_AND_APPENDING  # Append hints _B,_W,_BW to encoding labels
# (The "appending of _B,_W,_BW" will be carried out as this was the last assignment command.)

# Or, new spots, that are introduced into Mastodon, got this radius (size):
sim_cfg.AGENT_INITIAL_RADIUS = 1.5

# Or, this controls if the agents are allowed to move in z-axis at all.
sim_cfg.AGENT_DO_2D_MOVES_ONLY = Agent2dMovesRestriction.NO_Z_AXIS_MOVE  #does 2D in XY
# Btw, there are other options: NO_X_AXIS_MOVE, NO_Y_AXIS_MOVE, NO_RESTRICTION (does 3D)


# For a complete list, refer to the Java source code in the file:
# repository/src/main/java/org/mastodon/mamut/simulator/Simulator.java
# lines 23 till 72, the beginning of the class Simulator

# It is also possible to have the current configuration printed as Python
# code, ready for cut&paste... see below r.setPrintSettingsAsPythonCode()
# --------------------- own configuration ---------------------


# Now, let's simulate into the just-opened Mastodon app, assuming the loaded
# project is empty and thus:
# ...provide own number of cells and length of this simulation run
no_of_cells = 10
no_of_time_points = 80
flag_that_agents_should_be_seeded = True

# ...start the simulation (from the first available time point in the project)
r = Runner(project_model, no_of_cells, no_of_time_points, flag_that_agents_should_be_seeded)
r.changeConfigTo(sim_cfg)
r.setPrintSettingsAsPythonCode(True)
r.run()


# Let's simulate again into the very same existing Mastodon app, but
# now the Runner will run simulation from the last non-empty time point.
# It will thus continue where the previous run has ended.
#
# Continue also with the same simulation config, with the following changes,
# for example:
sim_cfg.AGENT_DO_2D_MOVES_ONLY = Agent2dMovesRestriction.NO_RESTRICTION
sim_cfg.AGENT_AVERAGE_LIFESPAN_BEFORE_DIVISION = int(sim_cfg.AGENT_AVERAGE_LIFESPAN_BEFORE_DIVISION * 1.5)
# NB: the example above slows down the growth rate by 50%, that exemplifies
#     how to introduce a change in the population behaviour

# Also a new length of the simulation run:
no_of_time_points = 90

r = Runner(project_model, -1, no_of_time_points)
r.changeConfigTo(sim_cfg)
r.run()

# ...and this pattern can continue...

# Note that the simulator will not save the created outcome, user must click
# the "Save" menu item explicitly in the opened Mastodon app.


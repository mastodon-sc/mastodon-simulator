#@File output_result_mastodon_project_file
#@int no_of_cells
#@int no_of_time_points

from org.mastodon.mamut.simulator.ui import Runner
from org.mastodon.mamut.simulator import SimulationConfig

# Example of running a brand new project that starts from scratch, and saves
# into a new .mastodon project eventually. It works without having a Mastodon
# app (visibly) around, no Mastodon windows should pop up.
r = Runner(output_result_mastodon_project_file.toString(), no_of_cells, no_of_time_points)

# Possibly adjusts the simulation configuration from the default configuration.
# (This is an optional step.)
sim_cfg = SimulationConfig()
sim_cfg.CREATE_MASTODON_CENTER_SPOT = False
r.changeConfigTo(sim_cfg)

# Run the simulation...
r.run()


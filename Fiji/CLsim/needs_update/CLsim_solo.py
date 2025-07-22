#@File outputResultMastodonProjectFile
#@int noOfCells
#@int noOfTimepoints

from org.mastodon.mamut.simulator.ui import Runner
from org.mastodon.mamut.simulator import SimulationConfig

# Example of running a brand new project that starts from scratch, and saves
# into a new .mastodon project eventually. It works without having a Mastodon
# app (visibly) around, no Mastodon windows should pop up.
r = Runner(outputResultMastodonProjectFile.toString(), noOfCells, noOfTimepoints)

# Possibly adjusts the simulation configuration from the default configuration.
# (This is an optional step.)
sim_cfg = SimulationConfig()
sim_cfg.CREATE_MASTODON_CENTER_SPOT = False
r.changeConfigTo(sim_cfg)

# Run the simulation...
r.run()


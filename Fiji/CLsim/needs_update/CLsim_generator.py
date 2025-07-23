#@Context ctx
#@File initial_mastodon_project_file

#@int number_of_time_points
#@int number_of_spots_per_timepoint
#@boolean do_link_spots

#@int reporting_starts_from_round
#@int reporting_every_Nth_round

from org.mastodon.mamut import MainWindow
from org.mastodon.mamut.io import ProjectLoader
from org.mastodon.mamut.util import NonSenseDataGenerator

# Loads the Mastodon project and shows the Mastodon app.
project_model = ProjectLoader.open(initial_mastodon_project_file.toString(), ctx, True, True)
MainWindow(project_model).setVisible(True)

# Runs the spots generator...
NonSenseDataGenerator(project_model, number_of_time_points, number_of_spots_per_timepoint, do_link_spots, reporting_starts_from_round, reporting_every_Nth_round)


#@Context ctx
#@File initialMastodonProjectFile

#@int numberOfTimepoints
#@int numberOfSpotsPerTimepoint
#@boolean doLinkSpots

#@int reportFromRound
#@int reportEveryNthRound

from org.mastodon.mamut import MainWindow
from org.mastodon.mamut.io import ProjectLoader
from org.mastodon.mamut.util import NonSenseDataGenerator

# Loads the Mastodon project and shows the Mastodon app.
projectModel = ProjectLoader.open(initialMastodonProjectFile.toString(), ctx, True, True)
MainWindow(projectModel).setVisible(True)

# Runs the spots generator...
NonSenseDataGenerator(projectModel, numberOfTimepoints, numberOfSpotsPerTimepoint, doLinkSpots, reportFromRound, reportEveryNthRound)


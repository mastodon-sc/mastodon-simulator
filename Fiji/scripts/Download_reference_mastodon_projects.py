#@ File (label="Where to place reference projects:", style="directory") output_folder
output_folder = output_folder.toString()

# open the console to see the "textual progress bar"
from ij import IJ
IJ.run("Console", "")

import urllib2
import zipfile
import os

project1 = 'reference_empty_dummydata.mastodon'
project2 = 'reference_empty_pixeldata.zip'
base_url = 'https://raw.githubusercontent.com/mastodon-sc/mastodon-simulator/refs/heads/main/Fiji/project_files/'

def download_plain_file(input_url, output_file_str):
    with open(output_file_str,'wb') as f:
        f.write( urllib2.urlopen(input_url).read() )
        f.close()

def unzip_file_to_its_containing_folder(input_zip_file_str):
    output_folder = os.path.dirname(input_zip_file_str)
    with zipfile.ZipFile(input_zip_file_str, 'r') as zip_ref:
        zip_ref.extractall(output_folder)


print "Downloading "+project1
proj1_file_path = os.path.join(output_folder, project1)
download_plain_file(base_url+project1, proj1_file_path)
print "Downloaded to "+proj1_file_path

print "Downloading "+project2
zip_file_path = os.path.join(output_folder, project2)
download_plain_file(base_url+project2, zip_file_path)
print "Downloaded to "+zip_file_path

print "Unzipping..."
unzip_file_to_its_containing_folder(zip_file_path)
print "Unzipped to "+output_folder
print "done."


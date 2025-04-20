import os
import platform
import subprocess

def open_qgis_project():
    # Path to the QGIS project, relative to this script
    script_dir = os.path.dirname(__file__)  # Directory of the script
    project_path = os.path.join(script_dir, 'Area', 'EgaleoGIS.qgz')  # Adjust the relative path as necessary

    # Determine the OS and set the QGIS path accordingly
    if platform.system() == 'Windows':
        # Windows: Update the path as per your QGIS installation
        qgis_executable = r'"C:\Program Files\QGIS 3.22.1\bin\qgis-bin.exe"'
    elif platform.system() == 'Darwin':
        # macOS
        qgis_executable = '/Applications/QGIS.app/Contents/MacOS/QGIS'
    elif platform.system() == 'Linux':
        # Linux: Assuming 'qgis' command is available in PATH
        qgis_executable = 'qgis'
    else:
        print(f"Unsupported operating system: {platform.system()}")
        return

    # Construct the command to open the QGIS project
    command = [qgis_executable, project_path]

    # For Windows, we need to ensure the path is correctly formatted as a string
    if platform.system() == 'Windows':
        command = ' '.join(command)

    # Execute the command
    try:
        if platform.system() == 'Windows':
            os.system(command)
        else:
            subprocess.run(command, check=True)
    except Exception as e:
        print(f"Failed to open the QGIS project: {e}")

if __name__ == '__main__':
    open_qgis_project()

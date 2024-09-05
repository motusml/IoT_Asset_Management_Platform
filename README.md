## IoT Asset Management Platform

IoT Asset Management Platform to monitor in real-time the state of industrial machines developed for the Multidisciplinary Project course at Politecnico di Milano in conjunction with Motus ml and Rai Way. In particular, you can add, manage and control the operation of the IoT sensors that monitor the various machines, displaying the latest data collected in real-time. Finally, it is possible to remotely upload/download AI models directly to the IoT sensors, visualising any anomalies in the machinery.

The involved students are:

* Daniele Asciutti
* Luca Brembilla 
* Bryan Alessandro Marchiori 
* Jaskaran Ram
* Gabriele Shu

## Platform deployment

The platform is build upon Docker. Once installed and run, open the terminal and run the following commands:

```
cd IoT_Asset_Management_Platform
bash start-docker.sh -cb

```
Then, open the browser and go to `localhost:3050`. The credentials for authentication are:

```
Username: Admin
Password: Admin
```

### Stop deploy
```
bash start-docker.sh -d
```

### Clean environment
```
bash start-docker.sh -c
```

You can find more information [here](/Documents)

## Testing the Platform
To simulate the addition of new devices, you have to run 2 python scripts in the PythonScripts folder. To do that, create a python virtual environment and use the following commands:

```
python3 -m venv <virtual-environment-name>
MAC: source <virtual-environment-name>/bin/activate
Windows (CMD): <virtual-environment-name>/Scripts/activate.bat
cd IoT_Asset_Management_Platform/PythonScripts
pip install -r requirements.txt
```

* To add a new device, run `python3 registerDevice.py`. Inside this file, you can specify the device name to add.
* To simulate the information sending or receiving to/from the device, run `python3 deviceLoop.py`. Inside this file, you can specify the device name query.

You can find more information [here](/PythonScripts/documents)




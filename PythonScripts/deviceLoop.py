from asyncCommunication import RequestHandler
import pickle

# Initialization to let the script work

# AsyncController url
url = "http://localhost:9096"

# Device name
name = "Device1"

handler = RequestHandler(name, url)

# change these parameters with the right ones
modelPath = "assets/new_model.pkl"
dataPath = "assets/temp_hum.line"
savePath = "assets/new_model.pkl"
data2Path = "assets/ai_model.line"
modelToSendName = "AI_model"


# This is the "real" loop

request = handler.openCommunication()
print(request)
if request == "UPDATE_MODEL":
    new_Model = handler.retrieveModel()
    pickle.dump(new_Model, open(savePath, "wb"))
elif request == "RETRIEVE_MODEL":
    with open(modelPath, 'rb') as f:
        model = pickle.load(f)
        handler.sendModel(modelToSendName, model)
elif request == "RETRIEVE_DATA":
    with open(dataPath, 'r') as f:
        with open(data2Path, 'r') as f2:
            data = f.read()
            data2 = f2.read()
            d = {"deviceData": data,
                 "modelData": data2}
            handler.sendData(d)
elif request == "NONE":
    print("nothing")
else:
    print("invalid request")

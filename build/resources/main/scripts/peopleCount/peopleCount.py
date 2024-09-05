import cv2
import json
import imutils
import requests
import numpy as np
from datetime import datetime

modelPath = "C:/source/Testing/yolo"
minConfig = 0.3
nmsThresh = 0.3

namesPath = "".join([modelPath, "/coco.names"])
configsPath = "".join([modelPath, "/yolov3.cfg"])
weightsPath = "".join([modelPath, "/yolov3.weights"])
LABELS = open(namesPath).read().strip().split("\n")

net = cv2.dnn.readNetFromDarknet(configsPath, weightsPath)
layerName = net.getLayerNames()
layerName = [layerName[i - 1] for i in net.getUnconnectedOutLayers()]


def detect_people(frame, net, ln, personIdx=0):
    (H, W) = frame.shape[:2]
    results = []
    blob = cv2.dnn.blobFromImage(frame, 1 / 255.0, (416, 416), swapRB=True, crop=False)
    net.setInput(blob)
    layerOutputs = net.forward(ln)

    boxes = []
    centroids = []
    confidences = []
    for output in layerOutputs:
        # loop over each of the detections
        for detection in output:
            scores = detection[5:]
            classID = np.argmax(scores)
            confidence = scores[classID]

            if classID == personIdx and confidence > minConfig:
                box = detection[0:4] * np.array([W, H, W, H])
                (centerX, centerY, width, height) = box.astype("int")

                x = int(centerX - (width / 2))
                y = int(centerY - (height / 2))

                boxes.append([x, y, int(width), int(height)])
                centroids.append((centerX, centerY))
                confidences.append(float(confidence))

    idxs = cv2.dnn.NMSBoxes(boxes, confidences, minConfig, nmsThresh)
    # ensure at least one detection exists
    if len(idxs) > 0:
        # loop over the indexes we are keeping
        for i in idxs.flatten():
            # extract the bounding box coordinates
            (x, y) = (boxes[i][0], boxes[i][1])
            (w, h) = (boxes[i][2], boxes[i][3])

            r = (confidences[i], (x, y, x + w, y + h), centroids[i])
            results.append(r)
    return results


def peopleCountMethod(frame, cameraId, channelName, postURL, fps):
    json_values = {}
    now = datetime.now()
    dt_string = now.strftime("%d%m%Y%H%M%S")
    frame = imutils.resize(frame, width=1000)
    result = detect_people(frame, net, layerName, personIdx=LABELS.index('person'))
    json_values["channelId"] = cameraId
    json_values["channelName"] = channelName
    json_values["entryExit"] = None
    json_values["entryViolationVos"] = None
    json_values["npr"] = None
    json_values["snapshot"] = None
    json_values["socialViolation"] = None
    json_values["peopleCount"] = {"channelName": channelName, "count": len(result), "id": cameraId,
                                  "time": dt_string}
    json_values["time"] = dt_string
    print(f'jsonValues, {json_values}')
    try:
        headers = {'Content-type': 'application/json', 'Accept': 'text/plain', 'CLIENT_KEY': 'ilens client key'}
        response = requests.post(url=postURL, data=json.dumps(json_values), headers=headers,
                                 verify=False)
        print("[INFO]: Captured Information Post Response : {}", response.status_code)
    except BaseException as e:
        raise ConnectionError("Exception {}", e)
    print(f'fps at sd, {int(fps)}')
    json_values.clear()
    # cv2.imshow("original Window", frame)
    # cv2.waitKey(1)

import os
import cv2
import json
import imutils
import requests
import numpy as np
from datetime import datetime
from scipy.spatial import distance as dist

#Dictionary defined for the pushing data
json_values = {}

# base path to YOLO directory that contains the all the required configuaration files.
MODEL_PATH = "yolo"

# set min confidence for, if it is person
MIN_CONF = 0.3
NMS_THRESH = 0.3

# use GPU for the fast processing it is the hardware process.
USE_GPU = False

#detecting people in the frame.
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

            if classID == personIdx and confidence > MIN_CONF:

                box = detection[0:4] * np.array([W, H, W, H])
                (centerX, centerY, width, height) = box.astype("int")

                x = int(centerX - (width / 2))
                y = int(centerY - (height / 2))

                boxes.append([x, y, int(width), int(height)])
                centroids.append((centerX, centerY))
                confidences.append(float(confidence))

    idxs = cv2.dnn.NMSBoxes(boxes, confidences, MIN_CONF, NMS_THRESH)
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

#find violatation between detected people in the frame based on the eculedian algorithm and pythogeros theoram.
def social_distance(minDist,post_api_url,camera_id,camera_url,place):

    MIN_DISTANCE = minDist
    now = datetime.now()
    dt_string = now.strftime("%d%m%Y%H%M%S")

    labelsPath = os.path.sep.join([MODEL_PATH, "coco.names"])
    LABELS = open(labelsPath).read().strip().split("\n")

    weightsPath = os.path.sep.join([MODEL_PATH, "yolov3.weights"])
    configPath = os.path.sep.join([MODEL_PATH, "yolov3.cfg"])

    net = cv2.dnn.readNetFromDarknet(configPath, weightsPath)
    if USE_GPU:
        net.setPreferableBackend(cv2.dnn.DNN_BACKEND_CUDA)
        net.setPreferableTarget(cv2.dnn.DNN_TARGET_CUDA)

    ln = net.getLayerNames()
    ln = [ln[i[0] - 1] for i in net.getUnconnectedOutLayers()]

    vs = cv2.VideoCapture(camera_url)
    writer = None
    data = []
    while True:
        (grabbed, frame) = vs.read()
        if not grabbed:
            break
        frame = imutils.resize(frame, width=700)
        results = detect_people(frame, net, ln,personIdx=LABELS.index("person"))
        total_no_of_people_in_frame = len(results)
        violate = set()
        if len(results) >= 2:
            centroids = np.array([r[2] for r in results])
            print("centroids\n",centroids)
            D = dist.cdist(centroids, centroids, metric="euclidean")
            for i in range(0, D.shape[0]):
                for j in range(i + 1, D.shape[1]):
                    if D[i, j] < MIN_DISTANCE:
                        violate.add(i)
                        violate.add(j)
        print("violated person",violate,"from,",total_no_of_people_in_frame,"total no. of people")

        for (i, (prob, bbox, centroid)) in enumerate(results):
            (startX, startY, endX, endY) = bbox
            (cX, cY) = centroid
            color = (0, 255, 0)
            if i in violate:
                color = (0, 0, 255)
            cv2.rectangle(frame, (startX, startY), (endX, endY), color, 2)
            cv2.circle(frame, (cX, cY), 5, color, 1)
        violated_people = len(violate)

        #data ready for pushing to api
        json_values["channelId"] = camera_id
        json_values["channelName"] = place
        json_values["entryExit"] = [{"id":"", "name": ""}]
        json_values["entryViolationVos"] = [{"id": " ", "violationDesc": "string"}]
        json_values["npr"] = {"values": ["string"]}
        json_values["snapshot"] = ""
        json_values["socialViolation"] = {"values": [{"Total_no_of_people_frame":total_no_of_people_in_frame, "violated_people" : violated_people}]}
        json_values["time"] = dt_string

        #cv2.imshow("Frame", frame)
        #key = cv2.waitKey(1) & 0xFF
        #if key == ord("q"):
        #    break

        try:
            headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
            response = requests.post(url=post_api_url, data=json.dumps(json_values), headers=headers, verify=False)
            print("Status:", response.status_code)
        except BaseException as e:
            print("Status:", e)

        json_values.clear()

#call = social_distance(500,"C:/source/ilens/src/main/resources/scripts/socialDistencing/sd1.jpg")
#print(call)


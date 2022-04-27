import cv2
import numpy as np
import imutils
modelPath = "C:/source/Testing/yolo"

namesPath = "".join([modelPath, "/coco.names"])
configsPath = "".join([modelPath, "/yolov3.cfg"])
weightsPath = "".join([modelPath, "/yolov3.weights"])

net = cv2.dnn.readNet(weightsPath, configsPath)
classes = []
with open(namesPath, "r") as f:
    classes = [line.strip() for line in f.readlines()]

layer_names = net.getLayerNames()
out_layers = [layer_names[i - 1] for i in net.getUnconnectedOutLayers()]

# To load the image for detection
url = "C:/source/Testing/yolo/truckhum.jpg"
img = cv2.imread(url)
img = imutils.resize(img, width=1000)
(height, width) = img.shape[:2]

# blob the image
blob = cv2.dnn.blobFromImage(img, 1/255.0, (416, 416), swapRB=True, crop=False)

# set the network for the img
net.setInput(blob)
outs = net.forward(out_layers)

# showing information on the screen
confidence = []
class_ids = []
boxes = []
for out in outs:
    for detection in out:
        scores = detection[5:]
        class_id = np.argmax(scores)
        confidences = scores[class_id]
        if confidences > 0.3:
            center_x = int(detection[0] * width)
            center_y = int(detection[1] * height)
            w = int(detection[2] * width)
            h = int(detection[3] * height)
            x = int(center_x - w / 2)
            y = int(center_y - w / 2)
            boxes.append([x, y, w, h])
            confidence.append(float(confidences))
            class_ids.append(class_id)
limitation = cv2.dnn.NMSBoxes(boxes, confidence, 0.3, 0.3)

for i in range(len(boxes)):
    if i in limitation:
        font = cv2.FONT_ITALIC
        x, y, w, h = boxes[i]
        label = classes[class_ids[i]]
        print(label)
        cv2.putText(img, label, (x - 10, y - 5), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 0, 0),2, cv2.LINE_AA)
        cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 255), 1)

cv2.imshow('The original img', img)
cv2.waitKey(0)

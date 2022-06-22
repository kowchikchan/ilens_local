import os, cv2, json, pickle, requests, urllib3, numpy as np
from datetime import datetime
from face_recognition import face_encodings
from face_recognition import face_locations
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)


def adjustGamma(image, gamma=1.0):
    invGamma = 1.0 / gamma
    table = np.array([((i / 255.0) ** invGamma) * 255
                      for i in np.arange(0, 256)]).astype("uint8")
    return cv2.LUT(image, table)


def checkContiguousOccurrence(matches):
    maximumCount = 0
    count = 0
    cntList = []
    for i in range(len(matches) - 1):
        if 'True' == matches[i] and matches[i] == matches[i + 1]:
            count += 1
            cntList.append(count + 1)
        else:
            count = 0
    if len(cntList) > 0:
        maximumCount = max(cntList)
    return maximumCount


def featuresAndLabels():
    modelFeature = []
    modelLabel = []
    path = os.path.dirname(os.path.abspath(__file__))
    for fileName in os.listdir(path):
        if fileName.endswith("Features.pickle"):
            loadedModelFeatures = pickle.load(open(os.path.join(path, fileName), 'rb'))
            for eachFeature in loadedModelFeatures:
                modelFeature.append(eachFeature)
        if fileName.endswith("Labels.pickle"):
            loadedModelLabel = pickle.load(open(os.path.join(path, fileName), 'rb'))
            for eachLabel in loadedModelLabel:
                modelLabel.append(eachLabel)
    print(f'Labels, {modelLabel}')
    return modelFeature, modelLabel


# trained Features and Labels
modelFeatures, modelLabels = featuresAndLabels()


class FRMethod:
    def __init__(self, frame, basePath, cameraId, place, entryOrExit, postURL, dataLocation, apiToken, startTime):
        self.frame = frame
        self.basePath = basePath
        self.cameraId = cameraId
        self.place = place
        self.entryOrExit = entryOrExit
        self.postURL = postURL
        self.dataLocation = dataLocation
        self.apiToken = apiToken
        self.startTime = startTime

    def liveMethod(self):
        now = datetime.now()
        dt_string = now.strftime("%d%m%Y%H%M%S")
        json_values = {}
        img = adjustGamma(self.frame, gamma=1.7)
        img = cv2.resize(img, (0, 0), fx=0.30, fy=0.30)
        faces = face_locations(img)
        # diff
        diff = datetime.now() - self.startTime
        print()
        if len(faces) != 0:
            encodesCurFrame = face_encodings(img, faces, model="large")
            for encodeFace, faceLoc in zip(encodesCurFrame, faces):
                matches = (np.linalg.norm(modelFeatures - encodeFace, axis=1) < .5)
                faceDistance = np.linalg.norm(modelFeatures - encodeFace, axis=1)
                matchList = [f'{str(i)}' for i in matches]
                contCount = checkContiguousOccurrence(matchList)
                print(f'contCount, {contCount}')
                # (top, right, bottom, left) = faceLoc
                json_values["channelId"] = self.cameraId
                json_values["channelName"] = self.place
                json_values["snapshot"] = dt_string
                json_values["time"] = dt_string
                json_values["type"] = self.entryOrExit
                if contCount >= 2:  # based on Training image quantity
                    matchIndex = np.argmin(faceDistance)
                    if matches[matchIndex]:
                        emp_id = modelLabels[matchIndex]
                        face_file_name = "".join([self.dataLocation, "/", dt_string, ".jpg"])
                        json_values["entryExit"] = [{"id": emp_id, "name": " "}]
                        json_values["entryViolationVos"] = None
                        json_values["npr"] = None
                        json_values["socialViolation"] = None
                        cv2.imwrite(face_file_name, cv2.resize(cv2.cvtColor(self.frame, cv2.COLOR_BGR2RGB), (490, 334),
                                                               interpolation=cv2.INTER_AREA))
                        try:
                            headers = {'Content-type': 'application/json', 'Accept': 'text/plain',
                                       'CLIENT_KEY': str(self.apiToken)}
                            response = requests.post(url=self.postURL, data=json.dumps(json_values), headers=headers,
                                                     verify=False)
                            diff1 = datetime.now() - self.startTime
                            print()
                            print("[INFO]: Captured Information Post Response : {}", response.status_code)
                        except ConnectionError as e:
                            raise ConnectionError("Connection Exception {}", e)
                else:
                    face_file_name = "".join([self.dataLocation, "/unknown_entries/", dt_string, ".jpg"])
                    cv2.imwrite(face_file_name, cv2.resize(cv2.cvtColor(self.frame, cv2.COLOR_BGR2RGB), (490, 334),
                                                           interpolation=cv2.INTER_AREA))
                    try:
                        headers = {'Content-type': 'application/json', 'Accept': 'text/plain',
                                   'CLIENT_KEY': str(self.apiToken)}
                        requests.post(url="https://127.0.0.1:8088/api/v1/ilens/unknown/save", data=json.dumps(json_values), headers=headers,
                                                 verify=False)
                    except ConnectionError as e:
                        raise ConnectionError("Connection Exception {}", e)
            json_values.clear()
        # else:
        #     # cv2.rectangle(img, (left, top), (right, bottom), (255, 0, 0), 2)
        #     # cv2.putText(img, 'unknown', (left - 10, top - 5), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 0, 0),
        #     #            2, cv2.LINE_AA)
        #     face_file_name = "".join([self.dataLocation, "/", dt_string, ".jpg"])
        #     cv2.imwrite(face_file_name, cv2.cvtColor(self.frame, cv2.COLOR_BGR2RGB))
        # print(f'{json_values}')
        # print(f'fps at fr, {int(self.fps)}')
        # json_values.clear()
        # img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
        # cv2.putText(img, f'fps : {int(self.fps)}', (25, 90), cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 0, 0), 2, cv2.LINE_AA)
        # cv2.imshow("Output Image", img)
        # cv2.waitKey(1)

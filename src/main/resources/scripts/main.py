import cv2, json, time, stomp, base64, argparse, threading
import numpy as np
from PIL import Image
from io import BytesIO
from faceDetection.frMethod import FRMethod
# from npr.npr_method import detect
# from peopleCount.peopleCount import peopleCountMethod


def client(idOfCamera, cameraUrl, dataApi):
    host = dataApi[:-6]
    port = dataApi[int(len(dataApi)) - 5:]
    clientConnection = stomp.Connection()
    clientConnection.set_ssl(for_hosts=[(host, port)])
    clientConnection.connect('admin', 'password', wait=True)
    videoCapture = cv2.VideoCapture(cameraUrl)
    while videoCapture.isOpened():
        ret, frame = videoCapture.read()
        # frame = cv2.resize(frame, (0, 0), fx=0.22, fy=0.22)
        if ret:
            _, buffer = cv2.imencode('.jpg', frame)
            try:
                encodedData = base64.b64encode(buffer)
                clientConnection.send(body=encodedData, destination='/queue/' + idOfCamera, headers={'persistent': 'true'})
            except UnicodeEncodeError:
                continue
    # time.sleep(2)
    # clientConnection.disconnect()


def server(appList, idOfCamera, basePath, channelName, frConfigs, postUrl, dataLocation, apiToken, dataApi):
    host = dataApi[:-6]
    port = dataApi[int(len(dataApi)) - 5:]

    class MyListener(stomp.ConnectionListener):
        def on_error(self, frame):
            print('error while receiving "%s"' % frame.body)

        def on_message(self, frame):
            frame = frame.body
            frameInBytes = bytes(frame, 'utf-8')
            decodedFrame = Image.open(BytesIO(base64.b64decode(frameInBytes)))
            finalDecodedImage = np.array(decodedFrame)

            if "fr" in appList:
                frObject = FRMethod(finalDecodedImage, basePath, idOfCamera, channelName, frConfigs, postUrl,
                                    dataLocation, apiToken)
                threading.Thread(target=frObject.liveMethod).start()

            # if "npr" in appsList:
            #     threading.Thread(target=detect(int(thresh_1), int(thresh_2), int(minRatio), int(maxRatio), frame,
            #     postUrl, channelName, cameraId, fps)).start()
            # if "peopleCount" in appsList:
            #     threadPeople = threading.Thread(target=peopleCountMethod(frame, cameraId, channelName, postUrl,
            #                                                              fps)).start()

    serverConnection = stomp.Connection()
    serverConnection.set_listener('', MyListener())
    serverConnection.set_ssl(for_hosts=[(host, port)])
    serverConnection.connect('admin', 'password', wait=True)
    serverConnection.subscribe(destination='/queue/' + idOfCamera, id=1, ack='auto')
    while True:
        time.sleep(1)


if __name__ == "__main__":
    argumentInput = argparse.ArgumentParser()
    argumentInput.add_argument("-i", "--jsonInput", required=True, help="JSON input string")
    argumentInput.add_argument("-b", "--basePath", required=True, help="base path")
    argumentInput.add_argument("-d", "--dataLocation", required=True, help="data location")
    inputData = argumentInput.parse_args()

    # read input json data.
    try:
        f = open(inputData.jsonInput, "r")
        data = json.loads(f.read())
        print(f'Configurations data {data}')
    except IOError as e:
        raise IOError("Error ", e)

    # basic camera Configuration
    postUrl = data['reportApi'] if str(data['reportApi']).endswith("/") else str(data['reportApi']) + "/"
    postUrl = postUrl + "api/v1/ilens/dataset"
    cameraId = data['id']
    cameraIp = data['ip']
    channelName = data['name']
    apiToken = data['apiToken']

    # base path and data location
    basePath = inputData.basePath
    dataLocation = inputData.dataLocation

    cameraURL = "rtsp://" + cameraIp + ":554/user=admin_password=tlJwpbo6_channel=1_stream=0.sdp?real_stream"
    print(f'Camera URL: {cameraURL}')

    # fr configurations
    appsList = []
    try:
        for value in data['executions']:
            appsList.append(value['type'])
        frConfigs = data['executions'][0]['config']
    except IndexError as e:
        raise IndexError("Value Error", e)

    # npr configurations
    minRatio = 0
    maxRatio = 0
    thresh_1 = 0
    thresh_2 = 0

    for i in range(len(data['executions'])):
        if data['executions'][i]['type'] == 'npr':
            try:
                minRatio = data['executions'][i]['config']['minRatio']
                maxRatio = data['executions'][i]['config']['maxRatio']
                thresh_1 = data['executions'][i]['config']['thresh1']
                thresh_2 = data['executions'][i]['config']['thresh2']
            except IndexError as e:
                raise KeyError("Value Error", e)
    # peopleCount configurations
    # null

    print(f'[INFO]: iLens Started.')
    print(f'[INFO]: Running Applications :  {appsList}')

    # Start client and server
    threading.Thread(target=client, args=(cameraId, cameraURL, data['dataApi'])).start()
    threading.Thread(target=server,
                     args=(appsList, cameraId, basePath, channelName, frConfigs, postUrl, dataLocation, apiToken,
                           data['dataApi'])).start()

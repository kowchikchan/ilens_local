import datetime
import cv2, json, stomp, base64, argparse, threading, asyncio, numpy as np
from PIL import Image
from io import BytesIO
from faceDetection.frMethod import FRMethod


# from npr.npr_method import detect
# from peopleCount.peopleCount import peopleCountMethod


async def publisher(idOfCamera, cameraUrl, dataApi):
    host = str(dataApi[:-6]).split("//")[1]
    port = dataApi[int(len(dataApi)) - 5:]
    clientConnection = stomp.Connection([(host, port)])
    clientConnection.connect('admin', 'password', wait=True)
    videoCapture = cv2.VideoCapture(cameraUrl)
    while True:
        await asyncio.sleep(0)
        ret, frame = videoCapture.read()
        if ret:
            _, buffer = cv2.imencode('.jpg', frame)
            try:
                encodedData = base64.b64encode(buffer)
                clientConnection.send(body=encodedData, destination='/queue/' + idOfCamera,
                                      headers={'persistent': 'true'})
                print()
            except UnicodeEncodeError:
                continue
    # time.sleep(2)
    # clientConnection.disconnect()


async def consumer(appList, idOfCamera, basePath, channelName, frConfigs, postUrl, dataLocation, apiToken, dataApi):
    host = str(dataApi[:-6]).split("//")[1]
    port = dataApi[int(len(dataApi)) - 5:]

    class MyListener(stomp.ConnectionListener):
        def on_error(self, frame):
            print('error while receiving "%s"' % frame.body)

        def on_message(self, frame):
            startTime = datetime.datetime.now()
            frame = frame.body
            frameInBytes = bytes(frame, 'utf-8')
            decodedFrame = Image.open(BytesIO(base64.b64decode(frameInBytes)))
            finalDecodedImage = np.array(decodedFrame)
            if frConfigs == 'entry':
                finalDecodedImage = finalDecodedImage[18:18 + 1060, 378:378 + 711]
            if frConfigs == 'exit':
                finalDecodedImage = finalDecodedImage[262:262 + 798, 898:898 + 683]
            if "fr" in appList:
                frObject = FRMethod(finalDecodedImage, basePath, idOfCamera, channelName, frConfigs, postUrl,
                                    dataLocation, apiToken, startTime)
                threading.Thread(target=frObject.liveMethod).start()

            # if "npr" in appsList:
            #     threading.Thread(target=detect(int(thresh_1), int(thresh_2), int(minRatio), int(maxRatio), frame,
            #     postUrl, channelName, cameraId, fps)).start()
            # if "peopleCount" in appsList:
            #     threadPeople = threading.Thread(target=peopleCountMethod(frame, cameraId, channelName, postUrl,
            #                                                              fps)).start()

    serverConnection = stomp.Connection([(host, port)])
    serverConnection.set_listener('', MyListener())
    serverConnection.connect('admin', 'password', wait=True)
    serverConnection.subscribe(destination='/queue/' + idOfCamera, id=1, ack='auto')
    while True:
        await asyncio.sleep(0)


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
        # print(f'Configurations data {data}')
    except IOError as e:
        raise IOError("Configurations Not Found ", e)

    # basic camera Configuration
    postUrl = data['reportApi'] if str(data['reportApi']).endswith("/") else str(data['reportApi']) + "/"
    postUrl = postUrl + "api/v1/ilens"
    cameraId, cameraIp, channelName, apiToken = data['id'], data['ip'], data['name'], data['apiToken']

    # base path and data location
    basePath, dataLocation = inputData.basePath, inputData.dataLocation
    cameraURL = "".join(["rtsp://", cameraIp, ":554/user=admin_password=tlJwpbo6_channel=1_stream=0.sdp?real_stream"])
    print(f'Camera: {cameraIp}')

    # fr configurations
    # appsList = []
    try:
        appsList = [value['type'] for value in data['executions']]
        frConfigs = data['executions'][0]['config']
    except IndexError as e:
        raise IndexError("Configurations Not Found.", e)

    # npr configurations
    minRatio, maxRatio, thresh_1, thresh_2 = 0, 0, 0, 0

    for i in range(len(data['executions'])):
        if data['executions'][i]['type'] == 'npr':
            try:
                minRatio = data['executions'][i]['config']['minRatio']
                maxRatio = data['executions'][i]['config']['maxRatio']
                thresh_1 = data['executions'][i]['config']['thresh1']
                thresh_2 = data['executions'][i]['config']['thresh2']
            except IndexError as e:
                raise KeyError("Configurations Not Found", e)
    # peopleCount configurations
    # null

    print(f'[INFO]: iLens Started.')
    print(f'[INFO]: Running Applications :  {appsList}')

    # Start client and server parallely.
    async def main():
        await asyncio.gather(
            publisher(cameraId, cameraURL, data['dataApi']),
            consumer(appsList, cameraId, basePath, channelName, frConfigs, postUrl, dataLocation, apiToken,
                   data['dataApi']))

    asyncio.run(main())

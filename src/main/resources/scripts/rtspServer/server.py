import cv2
import imutils
from flask import Flask, Response, request

app = Flask(__name__)


def getFrames(camIp):
    url = "rtsp://" + camIp + ":554/user=admin_password=tlJwpbo6_channel=1_stream=0.sdp?real_stream"
    camera = cv2.VideoCapture(url)
    while True:
        success, frame = camera.read()
        if not success:
            break
        else:
            ret, buffer = cv2.imencode('.jpg', imutils.resize(frame, width=393, height=258))
            frame = buffer.tobytes()
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')


@app.route('/<ip>')
async def main(ip):
    camIp = request.view_args['ip']
    return Response(getFrames(camIp), mimetype='multipart/x-mixed-replace; boundary=frame')


if __name__ == "__main__":
    context = ('ilensCert.crt', 'ilensKey.key')
    app.run(host='0.0.0.0', port=5000, threaded=True, ssl_context=context)

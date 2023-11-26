import base64
import cv2 as cv
import numpy as np

def test(value, height, width) -> str:
    arr = np.frombuffer(bytes(value), dtype=np.uint8)
    img = cv.imdecode(arr, cv.IMREAD_UNCHANGED)

    img = cv.cvtColor(img, cv.COLOR_RGB2BGR)
    img_gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
    img_resized = cv.resize(img_gray, None, fx=0.5, fy=0.5, interpolation=cv.INTER_AREA)
    img_result = cv.cvtColor(img_resized, cv.COLOR_BGR2RGB)
    _, b = cv.imencode('.png', img_result)

    return base64.b64encode(b.tobytes())
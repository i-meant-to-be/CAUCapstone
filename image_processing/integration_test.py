import numpy as np
import cv2 as cv
#import matplotlib
#matplotlib.use('TkAgg')
#import matplotlib.pyplot as plt

def test(img):
	img = cv.cvtColor(img, cv.COLOR_RGB2BGR)
	img_gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
	result = cv.resize(img_gray, None, fx=0.5, fy=0.5, interpolation=cv.INTER_AREA)
	result = cv.cvtColor(result, cv.COLOR_BGR2RGB)
	#_, b = cv.imencode('.png',result)
	return result.tobytes()
	#return result

'''
if __name__ == '__main__':
	img = cv.imread('jellybeannn.png')
	img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
	plt.imshow(test(img))
	plt.show()
'''

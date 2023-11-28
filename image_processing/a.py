from math import sqrt
import numpy as np
import cv2 as cv
#import matplotlib
#matplotlib.use('TkAgg')
#import matplotlib.pyplot as plt
from scipy.signal import argrelextrema as loc_max

'''
def showgraph_num(x_start,x_end,y_arr):
	x = np.arange(x_start, x_end)
	fig, ax = plt.subplots()
	ax.plot(x,y_arr)
	plt.show()

def showgraph_img(img):
	plt.imshow(img)
	plt.show()
'''

def separate_color(value, height, width, h):
	arr = np.frombuffer(bytes(value), dtype=np.uint8)
	img = cv.imdecode(arr, cv.IMREAD_UNCHANGED)
	img_bgr = cv.cvtColor(img, cv.COLOR_RGB2BGR)
	img_hsv = cv.cvtColor(img_bgr, cv.COLOR_BGR2HSV)
	
	kern_size = int(min(img.shape[:2])*0.004) # choose kernel size based on image dimension
	kern = np.ones((kern_size,kern_size), np.uint8) # kernel for erode/dilate; bigger number means larger erosion/dilation
	
	brighten = 60

	mask = None
	if h > 178 or h < 2:
		mask = cv.inRange(img_hsv, np.array([0,10,10]), np.array([1,255,255]))
		mask = cv.add(mask, cv.inRange(img_hsv, np.array([179,0,0]), np.array([180,255,255])a ))
	else:
		mask = cv.inRange(img_hsv, np.array([h,0,0]), np.array([h,255,255])) # mask for the i-th color(center[i])
	if kern_size > 1:
		mask = cv.dilate(cv.erode(mask, kern), kern) # erode then dilate mask to get rid of mess(pixelated parts)
	contours, hierarchy = cv.findContours(mask, cv.RETR_EXTERNAL, cv.CHAIN_APPROX_NONE) # create contour along mask
	
	selected = cv.bitwise_or(img_bgr, img_bgr, mask=mask)
	mask = cv.bitwise_not(mask)
	other = cv.bitwise_or(img_bgr, img_bgr, mask=mask)
	_,_,v = cv.split(cv.cvtColor(other, cv.COLOR_BGR2HSV))
	
	lim = 255 - brighten
	v[ v > lim ] = 255
	v[ v < lim ] += brighten
	
	other = cv.cvtColor(v, cv.COLOR_GRAY2BGR)
	img = cv.cvtColor(cv.drawContours(cv.add(selected, other),contours,-1,(0,0,0),2), cv.COLOR_BGR2RGB)
	_, a = cv.imencode('.png', img)
	
	return base64.b64encode(a.tobytes())
	
	
def content_isolation(img): #must be hsv image
	sat_thres = 45      # lower bound val~
	bright_thres = 100  # ~upper bound val
	mask = cv.inRange(img, np.array([0,0,bright_thres]), np.array([180,sat_thres,255]))
	
	white = cv.bitwise_not(np.zeros_like(img))
	white = cv.bitwise_or(white, white, mask=mask)
	mask = cv.bitwise_not(mask)
	
	img_ = cv.cvtColor(img, cv.COLOR_HSV2BGR)
	img_ = cv.bitwise_or(img_, img_, mask=mask)
	img_ = cv.add(img_,white)
	
	return img_

def process_image(value, height, width, k, high_pass_filter=False, reduce_glare=True):

	erode_color_mask = True
	bilateral_filter = True
	#high_pass_filter = False #aka 'document mode': fills low-saturation/low-brightness with white
	#reduce_glare = True
	
	arr = np.frombuffer(bytes(value), dtype=np.uint8)
	img = cv.imdecode(arr, cv.IMREAD_UNCHANGED)
	img = cv.cvtColor(img, cv.COLOR_RGB2BGR)
	
	# glare removal and bilateral filtering(noise removal)
	if reduce_glare:
		img_gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
		glare_thresh_ratio = 97     # percentile of pixel values to treat as glare (0~100)
		glare_thresh = np.percentile(img_gray.reshape(-1), glare_thresh_ratio)  # should be 0~255, probably >180
		#print(f'threshold for glare = {glare_thresh}')
		ret, inpaint_mask = cv.threshold(img_gray, glare_thresh, 255, cv.THRESH_BINARY)  # change to grayscale and apply threshold
		img = cv.inpaint(img, inpaint_mask, 3, cv.INPAINT_TELEA) # use radius of 3 with fast-marching method
	if high_pass_filter:
		img = content_isolation(cv.cvtColor(img, cv.COLOR_BGR2HSV))
	if bilateral_filter:
		img = cv.bilateralFilter(img, 15, 75, 75)
	#showgraph_img(cv.cvtColor(img, cv.COLOR_BGR2RGB))
	
	# prepare nparrays for optimal k value analysis and KNN color analysis
	# imgarr_sample = img.reshape((-1,3))
	img = cv.cvtColor(img, cv.COLOR_BGR2HSV)
	img_downscaled = cv.resize(img, (int(img.shape[1]/2),int(img.shape[0]/2)))
	
	# optimal k value analysis via elbow method
	# compactness index generation for k in range 2~16(inclusive)
	K_SEARCH_RANGE_START = 4
	K_SEARCH_RANGE_END = 16
	compact_results = []
	label_results = []
	center_results = []
	hue,_,_ = cv.split(img_downscaled)
	hue = hue.reshape((-1, 1))
	hue = np.float32(hue)
	for k in range(K_SEARCH_RANGE_START, K_SEARCH_RANGE_END+1, 2):
		criteria = (cv.TERM_CRITERIA_EPS + cv.TERM_CRITERIA_MAX_ITER, 10, 1.0) # iteration stop(termination) criteria: max iteration num, iteration stop thres(epsilon)
		ret,label,center = cv.kmeans(hue, k, None, criteria, 2, cv.KMEANS_RANDOM_CENTERS)
		label = label.reshape((-1))
	
		# label: array of each pixel's category info(0 ~ k-1)
		# center: BGR value of all k colors
		# ret: compactness measure
	
		compact_results.append(ret)
		label_results.append(label)
		center_results.append(center)
		#print(ret)
	
	#showgraph_num(K_SEARCH_RANGE_START, K_SEARCH_RANGE_END+1, compact_results)
	
	# normalize compactness results
	min_range = 0
	max_range = 10
	diff_range = max_range - min_range
	min_c = min(compact_results)
	max_c = max(compact_results)
	diff_c = max_c - min_c
	compact_normalized = []
	for i in compact_results:
		temp = ((i-min_c)*diff_range)/diff_c + min_range
		compact_normalized.append(temp)
	
	#showgraph_num(K_SEARCH_RANGE_START, K_SEARCH_RANGE_END+1, compact_normalized)
	
	# use elbow method on normalized array
	# let there be a line from (0,compact_normalized[0]) to (K_SEARCH_RANGE_END-1, compact_normalized[-1]).
	# resolve the distance between every point in the graph of (x, compact_normalized[x]) and the line.
	# the local maximum of this point-to-line distance graph is the elbow(knee) point.
	# fancy distance formula from https://stackoverflow.com/questions/40970478/distance-from-a-point-to-a-line
	diff_x = (K_SEARCH_RANGE_END - K_SEARCH_RANGE_START) // 2 
	diff_y = min_range - max_range
	point_line_distance = []
	den = sqrt(diff_y**2 + diff_x**2)
	
	for x,y in enumerate(compact_normalized):
		num = abs(diff_y*x - diff_x*y + diff_x*max_range)
		point_line_distance.append(num/den)
	

	dist_locmax = loc_max(np.array(point_line_distance), np.greater)[0] # list of local maxima of silhouette score graph

	
	# pick optimal k from point-line distance graph(apply compensation to later elements)
	a = 1.05                        # compensation constant
	m = dist_locmax[0]              # index for max val (= k-2)
	n = point_line_distance[m]*(a**(m*2))   # max silhouette score(initial val: when k=2(m=0))
	for i in dist_locmax:                # find biggest local maximum, but:
		current = point_line_distance[i]*(a**(i*2)) # allow up to 10% loss per step(k value increment) for later local maxima(for messy images)
		if n < current: 
			n = current
			m = i

	'''
	# check if there are any labels that are less than 1% of all pixels
	# if there is, then reduce m(index) by 1; effectively reducing the k value
	label_count_thres = hue.size*0.015
	if high_pass_filter: #if 'document mode' enabled, set tighter threshold
		label_count_thres = hue.size*0.017
	#print(f'label_count_thres:{label_count_thres}')
	blacklist = {} #blacklist dict for troublesome hue labels
	while m > 0:
		label, counts = np.unique(label_results[m], return_counts=True)
		centers = np.uint8(center_results[m]).flatten()
		for i,j in zip(label,counts):
			this_center = int(centers[i])
			#print(f'm={m}; Hue {this_center}: {j} pixels')
			#print(blacklist)
			if j < label_count_thres:
				cont = False
				for hues in blacklist:
					if min(abs(hues-this_center+180), abs(hues-this_center), abs(this_center-hues+180)) < 30:
						blacklist[hues] += 1
						if blacklist[hues] > 1:
							cont = True
						break
				else:
					blacklist[this_center] = 1
				if cont:
					continue
				m -= 1
				break
		else:
			break
	'''
 	
	k = m*2 + K_SEARCH_RANGE_START
	
	# perform KNN
	hue,sat,val = cv.split(img)
	hue = hue.reshape((-1, 1))
	hue = np.float32(hue)
	criteria = (cv.TERM_CRITERIA_EPS + cv.TERM_CRITERIA_MAX_ITER, 10, 1.0)
	ret,label,center = cv.kmeans(hue, k, None, criteria, 4, cv.KMEANS_RANDOM_CENTERS)
	
	# label: array of each pixel's category info(0 ~ k-1)
	# center: BGR value of all k colors
	# ret: compactness measure
	
	center = np.uint8(center)
	center = center.flatten()
	new_img = cv.merge((center[label.flatten()].reshape(img.shape[0],img.shape[1]), sat, val))
	#result = center[label.flatten()].reshape((img.shape))
	#result_with_contour = center[label.flatten()].reshape((img.shape))
	result = new_img
	#showgraph_img(cv.cvtColor(result, cv.COLOR_HSV2RGB))
	result_with_contour = new_img
	
	#make sure that center doesn't have duplicates
	center = np.unique(center)
	#print(center)
	k = center.size
	#print(f'actual k = {k}')

	# create and apply contour for areas of all k colors
	#mask_list = []
	#contour_list = []
	kern_size = int(min(img.shape[:2])*0.004) # choose kernel size based on image dimension
	#print(f'kern_size = {kern_size}')
	kern = np.ones((kern_size,kern_size), np.uint8) # kernel for erode/dilate; bigger number means larger erosion/dilation
	for i in range(k):
		if center[i] > 178 or center[i] < 2:
			mask = cv.inRange(result, np.array([0,10,10]), np.array([1,255,255]))
			mask = cv.add(mask, cv.inRange(result, np.array([179,0,0]), np.array([180,255,255]) ))
			#showgraph_img(mask)
		else:
			mask = cv.inRange(result, np.array([center[i],0,0]), np.array([center[i],255,255])) # mask for the i-th color(center[i])
		#mask_list.append(mask)
		if kern_size > 1 and erode_color_mask:
			mask = cv.dilate(cv.erode(mask, kern), kern) # erode then dilate mask to get rid of mess(pixelated parts)
		contours, hierarchy = cv.findContours(mask, cv.RETR_EXTERNAL, cv.CHAIN_APPROX_NONE) # create contour along mask
		cv.drawContours(result_with_contour, contours, -1, (0,0,0), 1) # -1 means draw all contour, (0,0,0) is color, last is line thickness
		#contour_list.append(contours)

	result_with_contour = cv.cvtColor(result_with_contour, cv.COLOR_HSV2RGB)
	_, a = cv.imencode('.png', result_with_contour)
	_, b = cv.imencode('.png', result)
	label = label.reshape((height, width))
	
	return [base64.b64encode(a.tobytes()), base64.b64encode(b.tobytes()), label, center]


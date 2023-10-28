import numpy as np
import cv2 as cv
import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
#from daltonize import daltonize as dlt #somehow works only in Windows???
import daltonize as dlt #use this in linux idk

def showgraph_num(x_start,x_end,y_arr):
	x = np.arange(x_start, x_end)
	fig, ax = plt.subplots()
	ax.plot(x,y_arr)
	plt.show()

def showgraph_img(img):
	plt.imshow(img)
	plt.show()

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
	
	showgraph_img(cv.cvtColor(img_, cv.COLOR_BGR2RGB))
	return img_

erode_color_mask = True
bilateral_filter = True
reduce_glare = True
high_pass_filter = False #aka 'document mode': fills low-saturation/low-brightness with white

filename = ''
while(True):
	filename = input('Input filename: ')
	img = cv.imread(filename)
	if img is None:
		print(f'Cannot open \'{filename}\'. Try again.')
		continue
	break

showgraph_img(cv.cvtColor(img, cv.COLOR_BGR2RGB))

k = 0
while(True):
	try:
		k = int(input('Input k value: '))
	except:
		print('Invalid input. Try again.')
		continue
	break

# glare removal and bilateral filtering(noise removal)
if reduce_glare:
	img_gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
	glare_thresh_ratio = 97     # percentile of pixel values to treat as glare (0~100)
	glare_thresh = np.percentile(img_gray.reshape(-1), glare_thresh_ratio)  # should be 0~255, probably >180
	print(f'threshold for glare = {glare_thresh}')
	ret, inpaint_mask = cv.threshold(img_gray, glare_thresh, 255, cv.THRESH_BINARY)  # change to grayscale and apply threshold
	img = cv.inpaint(img, inpaint_mask, 3, cv.INPAINT_TELEA) # use radius of 3 with fast-marching method
if bilateral_filter:
	img = cv.bilateralFilter(img, 15, 75, 75)
if high_pass_filter:
	img = content_isolation(cv.cvtColor(img, cv.COLOR_BGR2HSV))
showgraph_img(cv.cvtColor(img, cv.COLOR_BGR2RGB))

img = cv.cvtColor(img, cv.COLOR_BGR2HSV)

# perform KNN
hue,sat,val = cv.split(img)
hue = hue.reshape((-1, 1))
hue = np.float32(hue)
criteria = (cv.TERM_CRITERIA_EPS + cv.TERM_CRITERIA_MAX_ITER, 10, 1.0)
ret,label,center = cv.kmeans(hue, k, None, criteria, 10, cv.KMEANS_RANDOM_CENTERS)

# label: array of each pixel's category info(0 ~ k-1)
# center: BGR value of all k colors
# ret: compactness measure

center = np.uint8(center)
center = center.flatten()
new_img = cv.merge((center[label.flatten()].reshape(img.shape[0],img.shape[1]), sat, val))
result = new_img
showgraph_img(cv.cvtColor(result, cv.COLOR_HSV2RGB))
result_with_contour = new_img

#make sure that center doesn't have duplicates
center = np.unique(center)
print(center)
k = center.size
print(f'actual k = {k}')

# create and apply contour for areas of all k colors
mask_list = []
contour_list = []
kern_size = int(min(img.shape[:2])*0.004) # choose kernel size based on image dimension
print(f'kern_size = {kern_size}')
kern = np.ones((kern_size,kern_size), np.uint8) # kernel for erode/dilate; bigger number means larger erosion/dilation
for i in range(k):
	if center[i] > 178 or center[i] < 2:
		mask = cv.inRange(result, np.array([0,10,10]), np.array([1,255,255]))
		mask = cv.add(mask, cv.inRange(result, np.array([179,0,0]), np.array([180,255,255]) ))
		#showgraph_img(mask)
	else:
		mask = cv.inRange(result, np.array([center[i],0,0]), np.array([center[i],255,255])) # mask for the i-th color(center[i])
	mask_list.append(mask)
	if kern_size > 1 and erode_color_mask:
		mask = cv.dilate(cv.erode(mask, kern), kern) # erode then dilate mask to get rid of mess(pixelated parts)
	contours, hierarchy = cv.findContours(mask, cv.RETR_EXTERNAL, cv.CHAIN_APPROX_NONE) # create contour along mask
	cv.drawContours(result_with_contour, contours, -1, (0,0,0), 1) # -1 means draw all contour, (0,0,0) is color, last is line thickness
	contour_list.append(contours)

result = cv.cvtColor(result_with_contour, cv.COLOR_HSV2RGB)
showgraph_img(result)

# select color to view area by color
img = cv.cvtColor(img, cv.COLOR_HSV2BGR)
brighten = 60
sim_mode = False
while True:
	choice = input('input k value(1~%d or 0 for original) "sim={?|off|d|p|t}", else to quit: '%(k))
	if not choice.isnumeric():
		if 'sim=' in choice:
			key = choice.split('sim=')[-1]
			if key == '?':
				print(f'simulation set to {sim_mode if sim_mode else "off"}')
			elif key == 'off':
				sim_mode = False
			else:
				sim_mode = key
			continue
		else:
			print('quiting.')
			break
	choice = int(choice)
	if choice == 0:
		if sim_mode:
			f,ax = plt.subplots(1,2)
			ax[0].imshow(cv.cvtColor(img, cv.COLOR_BGR2RGB))
			ax[1].imshow(result)		
			sim_fig = dlt.simulate_mpl(f,color_deficit=sim_mode, copy=True)
			f_,ax_ = plt.subplots(1,1)
			ax_.imshow(cv.cvtColor(img, cv.COLOR_BGR2RGB))
			daltonize_fig = dlt.daltonize_mpl(f_, color_deficit=sim_mode)
			daltonize_fig = dlt.simulate_mpl(f_, color_deficit=sim_mode)
			plt.show()
		else:
			showgraph_img(result)
		continue
	if choice < 1 or choice > k:
		continue
	choice -= 1
	mask = mask_list[choice]
	selected = cv.bitwise_or(img, img, mask=mask)
	mask = cv.bitwise_not(mask)
	other = cv.bitwise_or(img, img, mask=mask)
	_,_,v = cv.split(cv.cvtColor(other, cv.COLOR_BGR2HSV))

	lim = 255 - brighten
	v[ v > lim ] = 255
	v[ v < lim ] += brighten

	other = cv.cvtColor(v, cv.COLOR_GRAY2BGR)
	temp = cv.cvtColor(cv.drawContours(cv.add(selected, other),contour_list[choice],-1,(0,0,0),2), cv.COLOR_BGR2RGB)
	if sim_mode:
		f,ax = plt.subplots(1,2)
		ax[0].imshow(result)
		ax[1].imshow(temp)
		sim_fig = dlt.simulate_mpl(f,color_deficit=sim_mode, copy=True)
		plt.show()
	else:
		showgraph_img(temp)

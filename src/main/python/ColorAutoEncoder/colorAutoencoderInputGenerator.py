import torch
from torch import nn
from torch.autograd import Variable
import matplotlib.pyplot as plt
import numpy as np

import sys
import json
import numpy
from ConvAutoencoder import ConvAutoencoder

def imshow(img):
    img = img / 2 + 0.5  
    plt.imshow(np.transpose(img, (1, 2, 0)))

# Example (from MM-NEAT): python .\src\main\python\AutoEncoder\autoencoderInputGenerator.py targetimage\skull6\snapshots\iteration30000.pth image
if __name__ == '__main__':
    
    modelToLoad = sys.argv[1] #'sim_autoencoder.pth'
    mode = sys.argv[2] # loss | image
    # loss : code prints MSELoss to console
    # image: code prints vector representation of image to console
    if mode != "loss" and mode != "image":
        print('mode must be either "loss" or "image"')
        quit()
    fixedModel = torch.load(modelToLoad)

    model = ConvAutoencoder().cuda()
    model.load_state_dict(fixedModel)
    criterion = nn.MSELoss()

    print("READY") # Java loops until it sees this special signal
    sys.stdout.flush() # Make sure Java can sense this output before Python blocks waiting for input

    inputImageDimension = 28
    channels = 3
    inputLength = inputImageDimension*inputImageDimension*channels

    while True:
        # Can't read one line at a time. Too long for console.
        #line = sys.stdin.readline()

        inputList = []
        # Loop through each pixel of image, store in a flat list
        for i in range(inputLength):
            inputList.append(float(sys.stdin.readline()))

        #print(len(inputList))

        lv = numpy.array(inputList)
        # Input is already a flat 1D array, so no new view needed
        input = torch.FloatTensor( lv ).reshape(1,channels,inputImageDimension,inputImageDimension).cuda()
        
        #imshow(input.detach().cpu().numpy()[0]) 
        #plt.show()
        
        output = model(Variable(input))
        loss = criterion(output, input)
        
        # Show image for troubleshooting
        #imshow(output.detach().cpu().numpy()[0]) 
        #plt.show()

        if mode == "image":
            print(json.dumps(output.reshape(1,-1).squeeze().tolist()))
        else: # should be "loss"
            print(loss.item())
        sys.stdout.flush() # Make Java sense output before blocking on next input
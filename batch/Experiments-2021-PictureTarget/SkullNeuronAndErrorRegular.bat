cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 trials:1 maxGens:100000 mu:100 io:true netio:true mating:true fs:true task:edu.southwestern.tasks.testmatch.imagematch.ImageMatchTask  matchImageFile:skull64.jpg allowMultipleFunctions:true ftype:0 watch:false netChangeActivationRate:0.3 cleanFrequency:400 logTWEANNData:false logMutationAndLineage:false recurrency:false overrideImageSize:false imageHeight:200 imageWidth:300 saveAllChampions:true useWoolleyImageMatchFitness:false useRMSEImageMatchFitness:true includeSigmoidFunction:true includeTanhFunction:false includeIdFunction:true includeFullApproxFunction:false includeApproxFunction:false includeGaussFunction:true includeSineFunction:true includeCosineFunction:true includeSawtoothFunction:false includeAbsValFunction:false includeHalfLinearPiecewiseFunction:false includeStretchedTanhFunction:false includeReLUFunction:false includeSoftplusFunction:false includeLeakyReLUFunction:false includeFullSawtoothFunction:false includeTriangleWaveFunction:false includeSquareWaveFunction:false blackAndWhitePicbreeder:true randomInitialMutationChances:20 trainingAutoEncoder:false base:targetimage log:TargetImage-SkullNeuronAndErrorRegular saveTo:SkullNeuronAndErrorRegular minNeuronFitness:true
cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 megaManAllowsConnectivity:false megaManAllowsSimpleAStarPath:true watch:false trials:1 mu:100 base:megamandistinctverticalandconnectivity log:MegaManDistinctVerticalAndConnectivity-CPPN2GAN saveTo:CPPN2GAN megaManGANLevelChunks:10 maxGens:50000 io:true netio:true GANInputSize:5 mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManCPPNtoGANLevelTask cleanOldNetworks:false useThreeGANsMegaMan:true MegaManGANUpperLeftModel:MegaManSevenGANUpperLeftCornerWith12TileTypes_5_Epoch5000.pth MegaManGANUpperRightModel:MegaManSevenGANUpperRightCornerWith12TileTypes_5_Epoch5000.pth MegaManGANLowerLeftModel:MegaManSevenGANLowerLeftCornerWith12TileTypes_5_Epoch5000.pth MegaManGANLowerRightModel:MegaManSevenGANLowerRightCornerWith12TileTypes_5_Epoch5000.pth MegaManGANUpModel:MegaManSevenGANUpWith12TileTypes_5_Epoch5000.pth MegaManGANDownModel:MegaManSevenGANDownWith12TileTypes_5_Epoch5000.pth MegaManGANHorizontalModel:MegaManSevenGANHorizontalWith12TileTypes_5_Epoch5000.pth allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:true includeSquareWaveFunction:true includeFullSawtoothFunction:true includeSigmoidFunction:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.megaman.MegaManMAPElitesDistinctVerticalAndConnectivityBinLabels steadyStateIndividualsPerGeneration:100

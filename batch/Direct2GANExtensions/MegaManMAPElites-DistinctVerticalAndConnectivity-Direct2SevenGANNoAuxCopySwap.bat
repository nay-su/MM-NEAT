cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 megaManAllowsSimpleAStarPath:true watch:false trials:1 mu:100 base:megamandistinctverticalandconnectivitynoauxcopyswap log:MegaManDistinctVerticalAndConnectivity-Direct2GANSevenGANNoAuxCopySwap saveTo:Direct2GANSevenGAN megaManGANLevelChunks:10 useMultipleGANsMegaMan:true MegaManGANUpperLeftModel:MegaManSevenGANUpperLeftCornerWith12TileTypes_5_Epoch5000.pth MegaManGANUpperRightModel:MegaManSevenGANUpperRightCornerWith12TileTypes_5_Epoch5000.pth MegaManGANLowerLeftModel:MegaManSevenGANLowerLeftCornerWith12TileTypes_5_Epoch5000.pth MegaManGANLowerRightModel:MegaManSevenGANLowerRightCornerWith12TileTypes_5_Epoch5000.pth MegaManGANUpModel:MegaManSevenGANUpWith12TileTypes_5_Epoch5000.pth MegaManGANDownModel:MegaManSevenGANDownWith12TileTypes_5_Epoch5000.pth MegaManGANHorizontalModel:MegaManSevenGANHorizontalWith12TileTypes_5_Epoch5000.pth megaManAllowsLeftSegments:true maxGens:100000 io:true netio:true GANInputSize:5 mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManGANLevelTask cleanOldNetworks:false cleanFrequency:-1 saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.RealValuedGenotype ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.megaman.MegaManMAPElitesDistinctVerticalAndConnectivityBinLabels steadyStateIndividualsPerGeneration:100 segmentSwapAuxiliaryVarialbes:true GANSegmentCopyMutationRate:0.4
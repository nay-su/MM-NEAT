# Possible to set "generation"
args = commandArgs(trailingOnly=TRUE)

setwd("../../zeldadungeonswallwaterrooms")
print("Load data")
types <- list("CPPN2GAN","CPPNThenDirect2GAN","Direct2GAN","Combined")

for(typePrefix in types) {
  #typePrefix <- "CPPN2GAN"
  
  for(i in 0:29) {
    dataFile <- paste(typePrefix,i,"/ZeldaDungeonsWallWaterRooms-",typePrefix,i,"_MAPElites_log.txt",sep="")
    map <- read.table(dataFile, na.strings = c("X"))
    print(dataFile)
    #map <- read.table(args[1])
    if (length(args)==0) {
      # Only the final archive matters
      lastRow <- map[map$V1 == nrow(map) - 1, ]
      nameEnd <- "LAST"
    } else {
      lastRow <- map[map$V1 == strtoi(args[1], base = 0L) - 1, ]
      nameEnd <- paste("Gen",args[1],sep="")
    }
	lastRow[ is.na(lastRow) ] <- -Inf
    archive <- data.frame(matrix(unlist(lastRow[2:length(lastRow)]), nrow=(length(lastRow)-1), byrow=T))
    names(archive) <- "PercentTraversed"

    
    # Change -Infinity to 0
    archive[archive<0] <- 0
    
    if(i > 0) {
      print("Add")
      averageArchive <- averageArchive + archive
    } else {
      print("Start")
      averageArchive <- archive
    }
  }
  
  archive <- averageArchive / 30
  


# Add data indicating how the data is binned, based on convention of how
# output data is organized

print("Organize bins")

maxNumRooms <- 25

wallBin <- append(rep(0, 10*(maxNumRooms+1)), rep(1, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(2, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(3, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(4, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(5, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(6, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(7, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(8, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(9, 10*(maxNumRooms+1)))

wallBin <- data.frame(wallBin)

waterBin <- append(rep(0, (maxNumRooms+1)), rep(1, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(2, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(3, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(4, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(5, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(6, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(7, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(8, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(9, (maxNumRooms+1)))

waterBin <- rep(waterBin, 10)
waterBin <- data.frame(waterBin)

roomBin <- rep(seq(0,maxNumRooms),10*10)
roomBin <- data.frame(roomBin)

allData <- data.frame(archive, wallBin, waterBin, roomBin)

###############################################

library(ggplot2)
library(dplyr)
library(viridis)
library(stringr)

dropUpperRightCorners <- filter(allData, wallBin + waterBin <= 9)
# Bin for dungeons with 0 rooms doesn't actually have anything
dropRooms0 <- filter(dropUpperRightCorners, roomBin > 0)

print("Create plot and save to file")

outputFile <- paste("ZeldaDungeonsWallWaterRooms-",typePrefix,"-AVG.",nameEnd,".heat.pdf",sep="")
#outputFile <- str_replace(args[1],"txt","heat.pdf")
pdf(outputFile)  
result <- ggplot(dropRooms0, aes(x=waterBin, y=wallBin, fill=PercentTraversed)) +
  geom_tile() +
  facet_wrap(~roomBin) +
  #scale_fill_gradient(low="white", high="orange") +
  scale_fill_viridis(discrete=FALSE) +
  xlab("Water Percentage Bin") +
  ylab("Wall Percentage Bin") +
  labs(fill = "Percent Rooms Traversed") +
  # Puts room count in the plot for each bin
  geom_text(aes(label = ifelse(wallBin == 5 & waterBin == 4, roomBin, NA)), 
            nudge_x = 2.5,nudge_y = 3) +
  #annotation_custom(grob) +
  theme(strip.background = element_blank(),
        strip.text = element_blank(),
        legend.position="top",
        legend.direction = "horizontal",
        legend.key.width = unit(70,"points"),
        panel.spacing.x=unit(0.001, "points"),
        panel.spacing.y=unit(0.001, "points"),
        axis.ticks = element_blank(),
        axis.text = element_blank())
print(result)
dev.off()

print(paste("Saved:",outputFile))
print("Finished")

}
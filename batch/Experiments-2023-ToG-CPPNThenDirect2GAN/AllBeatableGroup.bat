CALL AllBeatable.bat 29 mariolevelsdecoratensleniency MarioLevelsDecorateNSLeniency
CALL AllBeatable.bat 29 mariolevelsdistinctnsdecorate MarioLevelsDistinctNSDecorate
CALL AllBeatable.bat 29 zeldadungeonsdistinctbtrooms ZeldaDungeonsDistinctBTRooms
CALL AllBeatable.bat 29 zeldadungeonswallwaterrooms ZeldaDungeonsWallWaterRooms

Rscript.exe AverageBeatable.R MarioLevelsDecorateNSLeniency
Rscript.exe AverageBeatable.R MarioLevelsDistinctNSDecorate
Rscript.exe AverageBeatable.R ZeldaDungeonsDistinctBTRooms
Rscript.exe AverageBeatable.R ZeldaDungeonsWallWaterRooms
START Record.bat Subject-%1\Subject-%1-OriginalLevel
cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar zeldaType:original randomSeed:%1 > batch/Experiments-2019-ZeldaGAN/Subject-%1/Subject-%1-OriginalDungeon.txt
SLEEP 1000
taskkill /F /IM ffmpeg.exe /T
exit
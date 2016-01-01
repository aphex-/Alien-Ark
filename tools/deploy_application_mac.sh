cd ../project/desktop/
gradle dist
cd ../../tools

java -jar packr/packr.jar \
     -platform mac \
     -jdk "packr/openjdk-1.7.0-u45-unofficial-icedtea-2.4.3-macosx-x86_64-image.zip" \
     -executable AlienArk \
     -classpath ../project/desktop/build/libs/desktop-1.0.jar \
     -mainclass "com.nukethemoon.libgdxjam.desktop.DesktopLauncher" \
     -vmargs "-Xmx1G" \
     -resources ../project/android/assets/ \
     -minimizejre "soft" \
     -outdir packr/out

mv packr/out/Contents/MacOS/assets/* packr/out/Contents/MacOS/
rm -rf packr/out/Contents/MacOS/assets/
echo ================================
echo Deployed to 'packr/out/Contents'
echo ================================

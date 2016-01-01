cd ../project/desktop/
gradle dist
cd ../../tools

java -jar packr/packr.jar \
     -platform windows64 \
     -jdk "packr/openjdk-1.7.0-u45-unofficial-icedtea-2.4.3-windows-amd64-image.zip" \
     -executable AlienArk \
     -classpath ../project/desktop/build/libs/desktop-1.0.jar \
     -mainclass "com.nukethemoon.libgdxjam.desktop.DesktopLauncher" \
     -vmargs "-Xmx1G" \
     -resources ../project/android/assets/ \
     -minimizejre "soft" \
     -outdir packr/out/win64

mv packr/out/win64/assets/* packr/out/win64
rm -rf packr/out/win64/assets/
echo ================================
echo Deployed to 'packr/out/win64'
echo ================================

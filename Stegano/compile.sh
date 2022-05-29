mkdir out
cd out
javac -d . ../src/de/paulwolf/stegano/*.java ../src/de/paulwolf/stegano/ui/*.java ../src/de/paulwolf/stegano/core/*.java ../src/de/paulwolf/stegano/zip/*.java ../src/de/paulwolf/stegano/encrypt/*.java
cp ../rsc/* .
jar cmf ../rsc/META-INF/MANIFEST.MF ../Stegano.jar ./*
cd ..
rm -rf out

file=gradle.properties
target=VERSION_NAME=
cur_ver=`sed -n "s/^$target\(.*\)/\1/p" < $file`
read -p "Enter version name (current: $cur_ver): " new_ver
sed -i "" "s/$target.*/$target$new_ver/g" $file
./gradlew clean fcl-sample:assembleRelease fcl-sample:appDistributionUploadRelease
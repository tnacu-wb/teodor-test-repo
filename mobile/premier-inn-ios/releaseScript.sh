#!/bin/bash

if [ -z $3 ]
then
	echo Usage: sh releaseScript VERSION_NUMBER BUILD_NUMBER appstore/firebase
	exit
fi

agvtool new-marketing-version $1
agvtool new-version -all $2


if [ $3 == 'appstore' ]
then

    git stash
    git checkout develop
    git checkout -b release/$1-$2
    git tag $1-$2
    git commit -a -m \"release $1-$2\"
    git push
    git push origin --tags
    
	xcodebuild -project PremierInn.xcodeproj -scheme PremierInn -sdk iphoneos -configuration AppStoreDistribution archive -archivePath $PWD/build/PremierInn.xcarchive

	xcodebuild -exportArchive -archivePath $PWD/build/PremierInn.xcarchive -exportOptionsPlist export.plist -exportPath $PWD/build
	echo SCRIPT: EXPORT SUCCESS

	xcrun altool --upload-app -f ./build/PremierInn.ipa -u antoine.simon@whitbread.com -p zltt-hbnv-uodx-nxty
	echo SCRIPT: UPLOAD SUCCESS
elif [ $3 == 'firebase' ]
then
	xcodebuild -project PremierInn.xcodeproj -scheme PremierInn -sdk iphoneos -configuration Debug archive -archivePath $PWD/build/PremierInn.xcarchive
	echo SCRIPT: FIREBASE ARCHIVE SUCCESS

	xcodebuild -exportArchive -archivePath $PWD/build/PremierInn.xcarchive -exportOptionsPlist exportAdHoc.plist -exportPath $PWD/build -allowProvisioningUpdates
	echo SCRIPT: EXPORT SUCCESS
else
	echo SCRIPT: FAIL UPLOAD
fi

echo FINISHED
exit

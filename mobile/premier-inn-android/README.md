[![Build Status](https://app.bitrise.io/app/20edb208bad7752a/status.svg?token=MPFrEgNRNYycnzr7oYFxXQ&branch=develop)](https://app.bitrise.io/app/20edb208bad7752a)

# premier-inn-holborn-android
Rewrite of Premier Inn Android mobile app

Started in May 2016.

Why Holborn? Named after the location of our new office.

## Code Style

### XML
Following these ![best practices](https://jeroenmols.com/blog/2016/03/07/resourcenaming/) we'll try to achieve a xml mapping that should keep in mind the following structure 
* {WHERE} _ {description} _ {WHAT}

example:
```
review_booking_email_input
review_booking_surname_input
review_booking_logo_image
review_booking_user_label
review_booking_confirm_button
```
 
### JAVA
example:
```java
BindView(R.id review_booking_email_input) emailInput
BindView(R.id review_booking_ surname_input) surnameInput
BindView(R.id review_booking_ confirm_button) confirmButton
```
### Sqlite
http://www.sqlstyle.guide/

### Mapping types
example:
```
EditText ->  input
TextView -> text, label, heading
ImageView -> image
Button, CallToActionButton -> button
```
### CI pipeline scripts
https://github.com/whitbread-eos/jenkins-pipelines

### Postman collection
https://github.com/whitbread-eos/premierinn-api-collections

### SSL Pinning 
Process when new Cert needs to be deployed in api.whitbread.co.uk

1. Request a zip file of the new Cert to be deployed from Devops team usually (Chris Glaister)
2. This zip file should contain a list of files
    
    api-uat.whitbread.co.uk.intermediate
    
    api-uat.whitbread.co.uk.private
    
    api-uat.whitbread.co.uk.private.decrypt
    
    api-uat.whitbread.co.uk.public

3. Extract the Pins (new Cert's Public Keys) using PeerCertificateExtractorTest.java in order to add them in OkHttp Certificate Pinner.
    
    To be more specific execute this extractPeerCertificateFromPemTest() method and use the following files as inputs
    
    a) api-uat.whitbread.co.uk.intermediate
    
    b) api-uat.whitbread.co.uk.public
    
    Observer the output of each test and grab the sha256 pins

Note: This needs to be planned ahead of time i.e. Aprox a month pior to the Serves's Cert Release to make sure that all our customer have the latest version of the app.

Useful documentation of SSL pinning (Public Key Pinning)

https://medium.com/@develodroid/android-ssl-pinning-using-okhttp-ca1239065616
https://medium.com/@appmattus/android-security-ssl-pinning-1db8acb6621e
https://medium.com/@sreekumar_av/certificate-public-key-pinning-in-android-using-retrofit-2-0-74140800025b
https://www.madebymany.com/stories/a-year-of-react-native-ssl-pinning
https://scotthelme.co.uk/guidance-on-setting-up-hpkp/

 
### Remote Config - Deploy Cloud functions Example in firebase for FCM push

https://github.com/whitbread-eos/mobile-apps-common/tree/master/remote-config

## GCP (google cloud project) API keys and locations  

**Maps Api Key** - GCP: premier-inn-android-native  
> ✗ Unrestricted - for both com.whitbread.premierinn.stage & com.whitbread.premierinn  
https://console.cloud.google.com/apis/credentials?project=premier-inn-android-native  

**Places Api Key** - GCP: map-for-work-ext-71604843  
> ✔ Restricted for com.whitbread.premierinn  - AIzaSyAVZ06NHPlfhHAB-lEwvEc_GLi2z6OmmEo
https://console.cloud.google.com/apis/credentials?project=map-for-work-ext-71604843  
  
**Places Api Key** - GCP: premier-inn-e2418 (Aka Firebase)  
> ✗ Unrestricted for com.whitbread.premierinn.stage -  AIzaSyDp3R1tXrg04Z8LUaiz6Eoo6pqUF-6r_cY  
https://console.cloud.google.com/apis/credentials?project=premier-inn-e2418  

**Google Play Publish Api Key** - GCP: api-8901580560105178327-752451  
> https://console.cloud.google.com/apis/credentials?project=api-8901580560105178327-752451  
___
 For more details see :**app:build.gradle** 
 Use *accounts & passwords* confluence page for GCP credentials


# Branching model & Merging strategies

### Git flow

 - **develop** - active development branch
 - **feature/MON-x.x** - New feature  branch  (Usually derived from develop)
 - **master** - mirror of production
 - **release/x.x** - Release Candidate branch  (Usually derived from develop)
 - **rc/x.x** - Release Candidate feature branch / Parallel Development branch  (Usually derived from develop. This is usually when a **main feature** is close to a release but is going through added testing and code improvements)
 - **hotfix/x.x** - Emergency bug-fix branch  (Always derived from master)
               
*Day to day merging* 
 
 Merge **feature/x.x** or **bugfix/x.x** into base branch **develop**
> Create a PR and when approved then use "**squash and merge**" option from Github PR

**Create a Build for testers** 

To Create a build for testers please tag your last commit with Ready and the build will
 automatically begin and get uploaded to Firebase. 

*Merging on a release*

1. Merge  **release/x.x** or **hotfix/x.x**   into base branch  **master**  
> Create a PR and when approved then use "**merge pull request**" option from Github PR

2. Merge  **release/x.x**  or **hotfix/x.x** into base branch **develop**

> Create a PR and if there are no conflicts (rarely) then use "**merge pull request**" option from Github PR

**Otherwise use command line**            
```
git checkout develop
git merge --no-ff release/x.x (or hotfix/x.x)
```

*resolve conflicts locally*

```
git commit -m "resolving conflicts ..."
git push origin develop
``` 
(*observe that PR is automatically merged without additional action*)

**Note:** Do not forget to create a Github Release (i.e tag) for every release/hotfix before merging into master  
https://github.com/whitbread-eos/premier-inn-holborn-android/releases         


# Release Strategy - Stage Rollout - Monitoring

Every release apk gets uploaded automatically via bitrise to the Google Play Beta Channel. The release workflow used on Bitrise is release_to_beta. This workflow needs to be triggered manually, with the appropriate branch provided and the environment variable RELEASE_BUILD_NUMBER set. This environment variable represents how many releases there has been of this version to the Beta track in Google Play. 
The release_to_beta workflow will sign our APK with our upload key. The APK then available in Google Play will have been re-signed with a different key that we have given to Google. 

When regression testing is finished we manually move this apk from Beta track to Production using stage rollout strategy starting with a low percentage (10-15%)

We monitor Firebase Crashlytics and Analytics (Review and Book conversion rate)  

    Crashlytics: Check the crash rate that is stable and that no new issues have arisen.

    Analytics: Go to Funnels 
                    Select 'conversion_R&B' funnel and
                    ... filter by Platform (Android) and AppVersion (x.x.x) 
                    Check that the rate is close to 90% on a given date or range of dates since that release.

We let usually 2-3 days for every rollout for the users to adopt the release.

If everything from the above looks stable then we decide to either increase the rollout or halt it and investigate any problems
Repeat step 3 till we reach 100%.

Strategy - You can follow google proposed rollout every time you try to update (quite slow and safe)
or try to update rollout in 4 cycles (i.e 10-15% - 30% - 60% - 100%)

**Note:** These are guidance not rules so remeber that these steps could differ slightly depending the situation.
## TiMobile Android App Prototype

This is the prototype for the TiMobile Android App. This app is designed
in a way that it will be straightforward to integrate with the Actual APIs.

### What is included in the submission

The folder **TiMobile** contains the Android Studio project and can be imported directly into Android Studio.

### Sections implemented

Here is the complete list of sections which we have implemented

- 0-WelcomeScreen(x)
- 01_Login
- 01_Pre-Story
- 01_Story
- 01_Story0
- 1_Story1
- 1_Story2
- 2-Search
- 03_Browse_Story
- 03_Browse_Story_1
- 03_Story_Content0
- 03_Story_Content1-2
- 04_Selfie
- 04_Story_Completed
- 04_Story_Completed1
- 04_Story_Completed3
- 05_Profile
- 05_Profile0
- 05_Profile1
- 05_Profile2_2
- 05_Profile2
- 06_TI-Points
- 06_TI-Points1
- 07_Help
- 08_Card-Shop
- 09_Bookmark
- 09_Bookmark-1 and 09_Bookmark_2
- 09_Navigation
- 05_Profile


### Dependencies

We have used

- **Retrofit** and **RxJava** for Network
- **Dagger** for dependency injection
- **JAVA 8** via **RetroLambda**
- We are loading mock data from JSON files. This can be easily swapped with the real API
 implementation

To compile, you will need

- Android Studio v 3.0 or above
- Gradle version 4.1
- Android SDK 26

### Deployment

To build, either

- import the project in Android Studio and build

or from the command line

For debug build,

    $ ./gradlew assembleDebug installDebug

For release build,

    $ ./gradlew assembleRelease installRelease

The app starts on the login screen. you need to enter mock credentials that you can find below or in mockstring.xml file.

e.g. Email abc@gmail.com password 123456


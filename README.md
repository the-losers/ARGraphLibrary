
[![](https://jitpack.io/v/the-losers/ARGraphLibrary.svg)](https://jitpack.io/#the-losers/ARGraphLibrary)
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-ARGraph%20library-green.svg?style=flat )]( https://android-arsenal.com/details/1/7657 )

Yeah , Check us on Medium also :- https://medium.com/@deepakypro/how-to-plot-a-graph-in-real-world-using-arcore-android-4f5f1007fb86 

A simple library that allows you to add a graph in Real world using ARCore.

## Demo

![20190205_092505](https://user-images.githubusercontent.com/47303464/52292799-80422900-299b-11e9-9c1a-ccfb6b4c6618.gif)

## Gradle

Add following dependency to your root project `build.gradle` file:

```groovy
allprojects {
    repositories {
        ...
        jcenter()
        maven { url "https://jitpack.io" }
        ...
    }
}
```

Add following dependency to your app module `build.gradle` file:

```groovy
dependencies {
    ...
   implementation 'com.github.the-losers:ARGraphLibrary:x.y.z'
    ...
}
```

## Configure

* **Manifest File**

    * Permissions, you need two permissions , **1. Camera** so you can add a graph in real world,
                                              **2. Storage** to record and save the added graph video. 
    
    
     ```groovy
        <uses-permission android:name="android.permission.CAMERA"/>
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     ```
    
    * Enable ARCore in **Android studio**, There are two types of AR apps: **AR Required** and **AR Optional**.
    
        * AR Required apps
        
        ```groovy
           <application>
           …
           <!-- Indicates that app requires ARCore ("AR Required"). Causes Google
           Play Store to download and install ARCore when the app is installed.
           -->
   
           <meta-data android:name="com.google.ar.core" android:value="required" />
           </application>  
        ```
        * AR Optional apps
        
        ```groovy
          <application>
          …
          <!-- Indicates that app supports, but does not require ARCore ("AR Optional").
          Unlike "AR Required" apps, Google Play Store will not automatically
          download and install ARCore when the app is installed.
          -->
        
         <meta-data android:name="com.google.ar.core" android:value="optional" />
         </application>
         ```
        Read more about it here - [Enable ARCore](https://developers.google.com/ar/develop/java/enable-arcore)

* **build.gradle**

    * This library is using java 8 language features , so to enable them you will have to add below lines otherwise it will throw an error during compile time.
    
    ```groovy
     compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    ```
    
* **AndroidX**

    * ARGraph is using androidx library, So You will have to migrate your project to AndroidX. Read more about it here - [Migrating to AndroidX](https://developer.android.com/jetpack/androidx/migrate)
    
## Usage   

* Create the GraphConfig builder,
    
    ```groovy
     GraphConfig mGraphConfig = GraphConfig.newBuilder()
        .setGraphList(getList()) //pass the list that you want to add in real world
        .setEnableClassicPlatform(false) 
        .setEnableLogging(true) 
        .setEnableVideo(true)
        .setVideoQuality(VIDEO_QUALITY.QUALITY_1080P) //QUALITY_HIGH, QUALITY_2160P,QUALITY_1080P,QUALITY_720P,QUALITY_480P
        .build();
    ```
    
* Call the function `loadGraph`,
    
    ```groovy
     PlotGraph.get(context).loadGraph(mGraphConfig);
    ```
 
 * You can also check wheather a device support the arcore or not by simple calling the below method,
 
    ```groovy
    if(Utils.checkIsSupportedDeviceOrFinish(this)){
    //Do stuffs
    } else{
    //hide button or whatever you wants to do
    }
    ```

        
    


## App using ARGraph
[My Run Tracker - The Run Tracking App](https://play.google.com/store/apps/details?id=activity.com.myactivity2&hl=en_US)

## Author
[**Deepak Kumar**](https://github.com/deepakypro)

- Follow me on **Twitter**: [**@deepakypro**](https://twitter.com/deepakypro)
- Contact me on **LinkedIn**: [**deepakypro**](http://linkedin.com/in/deepakypro)

## License
ARGraphLibrary is available under the BSD license. See the [LICENSE](https://github.com/the-losers/ARGraphLibrary/blob/master/LICENSE.md) file.

<p align="center">
  <b>The End : Happy Coding :D</b>
</p>


<p align="center">
  <img src="https://user-images.githubusercontent.com/47303464/52298578-42e49800-29a9-11e9-8fd4-5897190e45b8.png">
</p>

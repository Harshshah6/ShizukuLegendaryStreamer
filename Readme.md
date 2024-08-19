# ShizukuLegendaryStreamer
LegendaryStreamerShizuku is an utility class for android java / kotlin application to communicate with shizuku for executing shell commands or adb command in the device. Also we can get access to restricted folder access without any extra permissions which is not an official way to access storage on android 11 and above.

 [![](https://jitpack.io/v/Harshshah6/ShizukuLegendaryStreamer.svg)](https://jitpack.io/#Harshshah6/ShizukuLegendaryStreamer)
 [![](https://jitpack.io/v/Harshshah6/ShizukuLegendaryStreamer/week.svg)](httls://jitpack.io/#Harshshah6/ShizukuLegendaryStreamer/week.svg)
 [![](https://jitpack.io/v/Harshshah6/ShizukuLegendaryStreamer/month.svg)](httls://jitpack.io/#Harshshah6/ShizukuLegendaryStreamer/month.svg)
 ![](https://badgen.net/github/release/Harshshah6/ShizukuLegendaryStreamer)
 ![GitHub repo size](https://img.shields.io/github/repo-size/Harshshah6/ShizukuLegendaryStreamer?color=g&logo=github)
 
<img src="https://raw.githubusercontent.com/Harshshah6/ShizukuLegendaryStreamer/main/img.png" width="300" alt="screentshot" class="GeneratedImage">

## Getting Started
Instructions on how to get the module in your project.

## Configure
### Maven
##### Step 1. Add the JitPack repository to your build file
  Add it in your root build.gradle at the end of repositories:
``` gradle
allprojects {
  repositories {
	   ...
	   maven { url 'https://jitpack.io' }
  }
}
 ```
 
#### Step 2. Add the dependency:
```gradle
dependencies {
   ...
   implementation 'com.github.Harshshah6:ShizukuLegendaryStreamer:1.0'
}
```

## Usage
```java
private LegendaryStreamerShizuku legendaryStreamerShizuku;
private final ExecutionProcessListener executionProcessListener = new ExecutionProcessListener() {
 	@Override
	public void onPreExecute() { }
        @Override
        public void onPostExecute(ArrayList<String> successMessages, ArrayList<String> errorMessages) { }
};
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        legendaryStreamerShizuku = new LegendaryStreamerShizuku(this);
	legendaryStreamerShizuku.autoReqPermission(); //Automatically request shizuku runtime permision to connect with shizuku

	legendaryStreamerShizuku.runCustomCommand("echo 'Hello World'",executionProcessListener); //Running an custom adb command
}
```

### Checkout the available methods [here](https://github.com/Harshshah6/ShizukuLegendaryStreamer/blob/main/LegendaryStreamerShizuku/src/main/java/legendary/streamer/shizuku/LegendaryStreamerShizuku.java)

<!-- or 
### Windows

Simply Download the latest GUI file from the **[releases](https://github.com/Harshshah6/InstaMedia-py/releases)** tab to use this project without any commands and by simple few clicks using our GUI application. -->


## Contributing
If you'd like to contribute to Project Title, here are some guidelines:

1. Fork the repository.
2. Create a new branch for your changes.
3. Make your changes.
5. Run the script to ensure they works.
6. Commit your changes.
7. Push your changes to your forked repository.
8. Submit a pull request.

<!-- 
## License
This project is licensed under the [License Name] - see the [LICENSE.md](LICENSE.md) file for details. -->

## Authors & Acknowledgments
<u>ShizukuLegendaryStreamer</u> was created by **[LEGENDARY STREAMER](https://github.com/Harshshah6)**.

- **Shizuku** :- [Shizuku](https://github.com/RikkaApps/Shizuku)

## **Changelog**

- **1.0:** Initial release (latest)

## **Contact**

If you have any questions or comments about InstaMedia, please contact **[LEGENDARY STREAMER](https://t.me/legendary_streamer_official)**.

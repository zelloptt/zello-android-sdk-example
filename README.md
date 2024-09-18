### Android Studio support

To use Zello’s SDK example app, you need Android Studio **Koala | 2024.1.1** or newer. Using a previous version of Android Studio Koala may prompt an error message like the following:

The project is using an incompatible version (AGP 8.3.1) of the Android Gradle plugin. Latest supported version is AGP 8.2.1

Please note that this example app **has not** been tested on earlier versions of Android Studio.

### Start

Once you’ve finalized the Gradle build scripts, open the project and press **CTRL + R** or use the run button in the Android Studio UI to run the example app on either an emulated or real device.

### Permissions

When the example app loads, you’ll be prompted to grant Zello authorization to three different permissions:

| **Permission**                                                 | **Recommended Authorization** |
| -------------------------------------------------------------- | ----------------------------- |
| “Allow Zello SDK Example App to record audio”                  | While using the app           |
| “Allow Zello SDK Example App to send you notifications”        | Allow                         |
| “Allow Zello SDK Example App to access this device’s location” | Allow                         |

Please be aware that Zello requires these permissions to properly operate. Failure to grant authorization may lead to unexpected behaviors in the SDK.

If embedding Zello inside your application, it’s your responsibility to determine what happens if a user doesn’t grant Zello the correct permissions.

### Connect

To utilize Zello’s SDK functionality, connect the example app to your Zello Work network:&#x20;

{% hint style="info" %}
**Please note:** This information is administered and managed by your company’s Zello Work network admin. For more information—including how to sign up for Zello Work—see the [Network Credentials](https://zelloptt.atlassian.net/wiki/x/DgB\_FQ) page.&#x20;
{% endhint %}

1. Tap **Connect** in the upper-right corner of your screen.
2. Enter your username, password, and network name.

Once signed in to Zello Work, you can send push-to-talk (PTT) messages to your direct contacts and channels.

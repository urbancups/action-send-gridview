# ActionSendGridview for Android

Standalone widget for retrieving and displaying the action_SEND intents on the user's device.

Use to share information with other apps
- Pulls the user's sharing options 
- Allows you to set the message / subject / url that will be sent.
- Allows you to set a precedence list so that you can determine the precedence in which intents will be shown.
- Initially shows 6 intents with a 'More' button that when clicked slides down in an animation to reveal the rest of the intents on the device.
 
#### Initial 6 sharing options
![Before clicking the more button - initial list of 6 intents](https://cloud.githubusercontent.com/assets/8603749/4184607/252dcd1c-374c-11e4-9bb5-d51b135f0756.jpg)

#### After clicking the more button
![After clicking the more button](https://cloud.githubusercontent.com/assets/8603749/4184608/25485df8-374c-11e4-8dcf-200ee113deb7.jpg)


## Usage

Include GridviewFragment in your layout xml as a new fragment

``` xml
<fragment
	android:layout_width="wrap_content"
	android:layout_height="wrap_content"
	android:id="@+id/coupon_sharing_fragment"
	android:name="com.citylifeapps.cups.actionsendgridview.GridviewFragment"/>
```
        
In order to properly display this widget, you should allocate about half the area of a standard phone screen.

In the onCreate method of your activity, before you call setcontentview, you should initialize some values for the widget using the static methods provided:

``` java
// Change the message text that you want to send 
GridviewFragment.setMsgPayload("This is the message text");

// Change the message subject that you want to send
GridviewFragment.setMsgSubject("This is the subject");

// Change the url that you want to send
GridviewFragment.setUrlPayload("www.tester.com");
```


## Precedents Of Intents

If you want to change the predefined order of the intents shown on the screen, you can define precedence for each intent, in case it's found.

Changing the precedence:

```
setPrecedence(intent_name, value);
```

- intent_name (String) - The intent_name should be the label name of the intent, for example Twitter.

- value (Integer) - The value should be the precedence of the intent whereby lower numbers are higher precedence and would appear sooner in the list. 

Once you are finished presetting the widget, you can display it onscreen by using 
``` java
setcontentview(R.layout.your_layout)
```
The default precedence list (if none is specified and passed to the widget) is "Twitter, Facebook Messenger, Copy to Clipboard, Facebook, Gmail, WhatsApp, Email". 



## Download

Clone the project from Github and reference it as a library project within your application  
(`file --> import module...` in android studio).


## License

```
Copyright 2014 Urbancups, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
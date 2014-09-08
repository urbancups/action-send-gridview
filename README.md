# ActionSendGridview for Android

 Standalone widget for retrieving and displaying the action_SEND intents on the user's device. 
 
 - Allows you to set the message/subject/url that will be sent. 
 
 - Allows you to set a precedence list so that you can determine the precedence in which intents will be shown.
 
 - Initially shows 6 intents with a 'More' button that when clicked slides down in an animation to reveal the rest of the intents on the device.
 

![Before clicking the more button - initial list of 6 intents](https://cloud.githubusercontent.com/assets/8603749/4184607/252dcd1c-374c-11e4-9bb5-d51b135f0756.jpg)

![After clicking the more button](https://cloud.githubusercontent.com/assets/8603749/4184608/25485df8-374c-11e4-8dcf-200ee113deb7.jpg)


## USAGE

Include GridviewFragment in your layout xml as a new fragment

```
<fragment
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:id="@+id/coupon_sharing_fragment"
android:name="com.citylifeapps.cups.actionsendgridview.GridviewFragment"/>
```
        
In order to properly display this widget, you should allocate about half the area of a standard phone screen.

In the onCreate method of your activity, before you call setcontentview, you should initialize some values for the widget using the static methods provided:

- Change the message text that you want to send:


    GridviewFragment.setMsgPayload("This is the message text");

- Change the message subject that you want to send:


    GridviewFragment.setMsgSubject("This is the subject");


- Change the url that you want to send:


    GridviewFragment.setUrlPayload("www.tester.com");



## PRECEDENCE OF INTENTS


Step one - create a new map<String,Integer>

    private final Map<String,Integer> precedenceMap= new HashMap<String,Integer>();


Step two - populate the map. The key should be the label of the intent (for example, WhatsApp, Gmail, etc.) - this does not have to be case sensitive. The value should be the precedence you want to give the intent in the list of intents, in which lower numbers appear before larger numbers. 

    precedenceMap.put("WhatsApp",1);


Step three - pass your precedenceMap to the widget:

    GridviewFragment.setPrecedenceMap(precedenceMap);

Once you are finished presetting the widget, you can display it onscreen by using 

    setcontentview(R.layout.your_layout)

The default precedence list (if none is specified and passed to the widget) is "Twitter, Facebook Messenger, Copy to Clipboard, Facebook, Gmail, WhatsApp, Email". 



## DOWNLOAD

Clone the project from Github and reference it as a library project within your application &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ('file->import module&#8230;' in android studio).


## LICENSE

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

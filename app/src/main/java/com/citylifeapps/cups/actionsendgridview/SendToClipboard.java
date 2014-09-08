package com.citylifeapps.cups.actionsendgridview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by CUPS on 06/09/2014
 */
public class SendToClipboard extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the application context
        Context context=this.getApplicationContext();

        //setup the clipboard manager
        final android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);

        //setup the clipboard data
        final android.content.ClipData clipData = android.content.ClipData
                .newPlainText("CUPS clipboard data", getIntent().getStringExtra("CupsText"));

        //make it the last item in clipboard data
        clipboardManager.setPrimaryClip(clipData);

        // alert the user that the text is in the clipboard and we're done
        Toast.makeText(this, R.string.sendtoclipboard_textcopied, Toast.LENGTH_SHORT).show();

        finish();
    }
}

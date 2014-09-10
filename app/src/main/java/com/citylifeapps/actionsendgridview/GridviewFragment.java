package com.citylifeapps.actionsendgridview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Yonatan Moskovich on 07/09/2014
 */
public class GridviewFragment extends Fragment {

    private GridView gridView; // a gridview of all the intents we want to show to the user
    private final List<IntentShare> intentShareListAll=new ArrayList<IntentShare>();
    private final List<IntentShare> intentShareListToUse=new ArrayList<IntentShare>();
    private Button btnMore; // the More button - to show the long list of intents
    private boolean bMoreWasClicked=false; // did the user click on the More button?
    private LinearLayout llCouponSharingMainMore;
    private LinearLayout llCouponSharingSubMore;
    private AsyncPopulateIntents asyncPopulateIntents=new AsyncPopulateIntents();
    private ObjectAnimator animation;
    private boolean bAnimationCancelled=false;

    static String msgSubject="Put some subject here";
    static String msgPayload="Put some text here"; //text to be sent by the user
    static String urlPayload="www.test.com";
    static Map<String,Integer> precedenceMap= new HashMap<String,Integer>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View FragmentToDisplay = inflater.inflate(R.layout.gridview_fragment, container, false);

        gridView = (GridView) FragmentToDisplay.findViewById(R.id.gridCouponSharing);
        btnMore=(Button) FragmentToDisplay.findViewById(R.id.btnCouponSharingMore);
        llCouponSharingMainMore=(LinearLayout) FragmentToDisplay.findViewById(R.id.llCouponSharingMainMore);
        llCouponSharingSubMore=(LinearLayout) FragmentToDisplay.findViewById(R.id.llCouponSharingSubMore);

        PackageManager packageManager=getActivity().getPackageManager();

        //POPULATE A LIST OF ALL SHARE INTENTS
        Intent shareIntentTemplate = new Intent(Intent.ACTION_SEND);
        shareIntentTemplate.setType("text/plain");

        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(shareIntentTemplate, 0);

        if (!resolveInfoList.isEmpty()) {

            Drawable drawable;
            Bitmap bitmap;


            // create a list of intentShare objects from the resolveInfo list
            for (ResolveInfo resolveInfo : resolveInfoList) {
                IntentShare intentShare=new IntentShare();

                intentShare.setPrecedenceMap(precedenceMap);

                String packageName=resolveInfo.activityInfo.packageName;
                String loadLabel=resolveInfo.activityInfo.loadLabel(packageManager).toString();

                intentShare.setPackageName(packageName);

                drawable = resolveInfo.loadIcon(packageManager);
                bitmap = ((BitmapDrawable) drawable).getBitmap();

                final float scale = this.getResources().getDisplayMetrics().density;
                int pixels = (int) (50 * scale + 0.5f);

                Drawable scaledDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, pixels, pixels, true));

                intentShare.setBitmap(scaledDrawable);


                if (packageName.equals("com.facebook.orca")) {
                    intentShare.setLabel("Facebook Messenger");
                    intentShare.setPayload(urlPayload);
                }
                else if (packageName.equals("com.google.android.apps.docs") && loadLabel.equals("Copy to clipboard")) {
                    intentShare.setLabel("Copy to Clipboard");

                    Intent clipboardIntent = new Intent(getActivity(), com.citylifeapps.actionsendgridview.SendToClipboard.class);
                    clipboardIntent.putExtra("CupsText", msgPayload);

                    intentShare.setIntent(clipboardIntent);
                }
                else {
                    intentShare.setLabel(loadLabel);
                    intentShare.setPayload(msgPayload);
                }

                intentShare.setSubject(msgSubject);
                intentShareListAll.add(intentShare);
            }

            //sort the list of intents by package name so that we get consistent results every time we run
            Collections.sort(intentShareListAll, new Comparator<IntentShare>() {
                public int compare(IntentShare result1, IntentShare result2) {
                    return result1.getPrecedence() - result2.getPrecedence();
                }
            });

            asyncPopulateIntents.execute();
        }


        return FragmentToDisplay;
    }

    public static void setMsgPayload(String inpMsgPayload) {
        msgPayload=inpMsgPayload;
    }

    public static void setMsgSubject(String inpMsgSubject) {
        msgSubject=inpMsgSubject;
    }

    public static void setUrlPayload(String inpUrlPayload) {
        urlPayload=inpUrlPayload;
    }

    public static void setPrecedence(String key, Integer value) {
        if (precedenceMap.containsKey(key)) {
            precedenceMap.remove(key);
            precedenceMap.put(key, value);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CREATE A PRECEDENCE MAP SO WE CAN SET THE ORDER OF ICONS IN THE ACTIVITY
        precedenceMap.put("Twitter",0);
        precedenceMap.put("Facebook Messenger",1);
        precedenceMap.put("Copy to Clipboard",2);
        precedenceMap.put("Facebook",3);
        precedenceMap.put("Gmail",4);
        precedenceMap.put("WhatsApp",5);
        precedenceMap.put("Email",6);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (bAnimationCancelled) {
            animation.start();
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startActivity(intentShareListToUse.get(position).getIntent());
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bMoreWasClicked) {

                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int height = size.y;

                    int heightRelativeToTop = getRelativeTop(llCouponSharingMainMore);

                    bMoreWasClicked = true;

                    asyncPopulateIntents=new AsyncPopulateIntents();
                    asyncPopulateIntents.execute();

                    animation = ObjectAnimator.ofFloat(llCouponSharingSubMore, "y", heightRelativeToTop, height);

                    animation.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            btnMore.setVisibility(View.GONE);

                            if (bAnimationCancelled) {
                                bAnimationCancelled=false;
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animation.setInterpolator(new AccelerateInterpolator());
                    animation.setDuration(800);
                    animation.start();

                }
            }
        });

    }

    private void buildIntentList() {

        if (!bMoreWasClicked) { //ASSEMBLE THE SHORT LIST


            for (IntentShare intentShare : intentShareListAll) {

                if (intentShareListToUse.size() < 6) {
                    intentShareListToUse.add(intentShare);
                }
            }

        } else { //ASSEMBLE THE LONG LIST

            intentShareListToUse.clear();
            intentShareListToUse.addAll(intentShareListAll);
        }

    }

    private void displayIntentList() {
        GridAdapter gridAdapter = new GridAdapter(intentShareListToUse);
        gridView.setAdapter(gridAdapter);
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    private class AsyncPopulateIntents extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if (!isCancelled()) {
                buildIntentList();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            displayIntentList();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        // stopping the activity so cancel the animation if it's running
        if (animation!=null && animation.isRunning()) {
            animation.cancel();
            bAnimationCancelled=true;
        }

        // stopping the activity so cancel the asynctask if it's running
        asyncPopulateIntents.cancel(true);
    }

}

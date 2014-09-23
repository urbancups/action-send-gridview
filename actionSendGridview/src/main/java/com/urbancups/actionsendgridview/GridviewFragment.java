package com.urbancups.actionsendgridview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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

    private final int ANIMATION_DURATION=800;
    private static final Map<String, Integer> precedenceMap = new HashMap<String, Integer>();
    private static String msgSubject = "Put some subject here";
    private static String msgPayload = "Put some text here"; //text to be sent by the user
    private static String urlPayload = "www.test.com";
    private final List<IntentShare> intentShareListAll = new ArrayList<IntentShare>();
    private final List<IntentShare> intentShareListToUse = new ArrayList<IntentShare>();
    private GridAdapter gridAdapter;
    private boolean bMoreWasClicked = false; // did the user click on the More button?
    private AsyncPopulateIntents asyncPopulateIntents = new AsyncPopulateIntents();
    private ObjectAnimator animation;
    private boolean bAnimationCancelled = false;

    private GridView gridView; // a gridview of all the intents we want to show to the user
    private Button btnMore; // the More button - to show the long list of intents
    private LinearLayout llCouponSharingMainMore;
    private LinearLayout llCouponSharingSubMore;

    @SuppressWarnings("unused")
    public static void setMsgPayload(String inpMsgPayload) {
        msgPayload = inpMsgPayload;
    }

    @SuppressWarnings("unused")
    public static void setMsgSubject(String inpMsgSubject) {
        msgSubject = inpMsgSubject;
    }

    @SuppressWarnings("unused")
    public static void setUrlPayload(String inpUrlPayload) {
        urlPayload = inpUrlPayload;
    }

    @SuppressWarnings("unused")
    public static void setPrecedence(String key, Integer value) {
        if (precedenceMap.containsKey(key)) {
            precedenceMap.remove(key);
            precedenceMap.put(key, value);
        } else {
            precedenceMap.put(key, value);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //inflate the layout
        View FragmentToDisplay = inflater.inflate(R.layout.gridview_fragment, container, false);

        gridView = (GridView) FragmentToDisplay.findViewById(R.id.gridCouponSharing);
        gridView.setAdapter(gridAdapter);

        btnMore = (Button) FragmentToDisplay.findViewById(R.id.btnCouponSharingMore);
        llCouponSharingMainMore = (LinearLayout) FragmentToDisplay.findViewById(R.id.llCouponSharingMainMore);
        llCouponSharingSubMore = (LinearLayout) FragmentToDisplay.findViewById(R.id.llCouponSharingSubMore);

        //draw the intents in the grid
        asyncPopulateIntents.execute();

        return FragmentToDisplay;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //build a default precedence map so we can set the order of icons in the activity
        precedenceMap.put("Twitter", 0);
        precedenceMap.put("Facebook Messenger", 1);
        precedenceMap.put("Copy to Clipboard", 2);
        precedenceMap.put("Facebook", 3);
        precedenceMap.put("Gmail", 4);
        precedenceMap.put("WhatsApp", 5);
        precedenceMap.put("Email", 6);

        //populate a list of all share intents
        Intent shareIntentTemplate = new Intent(Intent.ACTION_SEND);
        shareIntentTemplate.setType("text/plain");

        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(shareIntentTemplate, 0);
        boolean isIntentSafe = resolveInfoList.size() > 0;

        if (isIntentSafe) {

            Drawable drawable;
            Drawable scaledDrawable;
            Bitmap bitmap;

            String packageName;
            String loadLabel;
            IntentShare intentShare;

            final float scale = this.getResources().getDisplayMetrics().density;

            gridAdapter=new GridAdapter(scale);

            //get pixel size for 50dp
            final int dpPixels = (int) (50 * scale + 0.5f);


            // create a list of intentShare objects from the resolveInfo list
            for (ResolveInfo resolveInfo : resolveInfoList) {

                //initialize intentshare
                intentShare = new IntentShare();

                //set to the precedence map
                intentShare.setPrecedenceMap(precedenceMap);

                //get package information
                packageName = resolveInfo.activityInfo.packageName;
                intentShare.setPackageName(packageName);

                //get the intent icon
                drawable = resolveInfo.loadIcon(packageManager);
                bitmap = ((BitmapDrawable) drawable).getBitmap();
                scaledDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, dpPixels, dpPixels, true));
                intentShare.setBitmap(scaledDrawable);

                //get the subject
                intentShare.setSubject(msgSubject);

                //logic for facebook messenger
                if (packageName.equals("com.facebook.orca")) {
                    intentShare.setLabel("Facebook Messenger");
                    intentShare.setPayload(urlPayload);

                    //logic for clipboard
                } else if (resolveInfo.activityInfo.loadLabel(packageManager).toString().equals("Copy to clipboard")) {
                    intentShare.setLabel("Copy to Clipboard");

                    Intent clipboardIntent = new Intent(getActivity(), SendToClipboard.class);
                    clipboardIntent.putExtra("CupsText", msgPayload);

                    intentShare.setIntent(clipboardIntent);

                    //logic for all other intents
                } else {
                    //get the intent label
                    loadLabel = resolveInfo.activityInfo.loadLabel(packageManager).toString();
                    intentShare.setLabel(loadLabel);
                    intentShare.setPayload(msgPayload);
                }

                intentShareListAll.add(intentShare);
            }

            //sort the list of intents by package name so that we get consistent results every time we run
            Collections.sort(intentShareListAll, new Comparator<IntentShare>() {
                public int compare(IntentShare result1, IntentShare result2) {
                    return result1.getPrecedence() - result2.getPrecedence();
                }
            });

        }

    }

    @Override @SuppressLint("NewApi")
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

                    bMoreWasClicked = true;

                    asyncPopulateIntents = new AsyncPopulateIntents();
                    asyncPopulateIntents.execute();

                    //get the size of the screen
                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point size = new Point();

                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
                        display.getSize(size);
                    } else {
                        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
                    }

                    int heightOfScreen = size.y;

                    int heightRelativeToTop = getRelativeTop(llCouponSharingMainMore);


                    //create the animation
                    animation = ObjectAnimator.ofFloat(llCouponSharingSubMore, "y", heightRelativeToTop, heightOfScreen);

                    animation.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            btnMore.setVisibility(View.GONE);

                            if (bAnimationCancelled) {
                                bAnimationCancelled = false;
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
                    animation.setDuration(ANIMATION_DURATION);
                    animation.start();

                }

            }
        });

    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    @Override
    public void onPause() {
        super.onPause();

        // stopping the activity so cancel the animation if it's running
        if (animation != null && animation.isRunning()) {
            animation.cancel();
            bAnimationCancelled = true;
        }

        // stopping the activity so cancel the asynctask if it's running
        asyncPopulateIntents.cancel(true);
    }

    private class AsyncPopulateIntents extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if (!isCancelled()) {

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

                gridAdapter.setAdapterList(intentShareListToUse);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            gridAdapter.notifyDataSetChanged();
        }

    }

}
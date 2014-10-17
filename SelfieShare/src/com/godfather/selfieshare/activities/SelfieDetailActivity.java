package com.godfather.selfieshare.activities;

import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.godfather.selfieshare.R;
import com.godfather.selfieshare.controllers.Message;
import com.godfather.selfieshare.models.Selfie;

import java.util.ArrayList;

public class SelfieDetailActivity extends BaseActivity<SelfieDetailActivity> implements View.OnTouchListener {
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    private Selfie selfie;
    private Message message;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_selfie_detail;
    }

    @Override
    protected void create() {
        this.message = new Message(this);

        ImageView imageView = (ImageView)this.findViewById(R.id.imageView);
        Bundle extras = getIntent().getExtras();
        selfie = extras.getParcelable("selfie");
        imageView.setImageBitmap(selfie.getPictureBitmap());

        // Bind click listeners
        imageView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                matrix.set(view.getImageMatrix());
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;

                handler.postDelayed(mLongPressed, 1000);
                break;

            case MotionEvent.ACTION_UP: // first finger lifted
                handler.removeCallbacks(mLongPressed);

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:
                handler.removeCallbacks(mLongPressed);

                if (mode == DRAG)
                {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                }
                else if (mode == ZOOM)
                {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f)
                    {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event)
    {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
        {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++)
        {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }

    final Handler handler = new Handler();
    Runnable mLongPressed = new Runnable() {
        @Override
		public void run() {
            Log.i("", "Long press!");
            onLongClick();
        }
    };


    public boolean onLongClick() {
        final String[] contactData = selfie.getContactData();

        if(!isInContacts(contactData[1])) {
            new AlertDialog.Builder(this)
                    .setTitle("Add contact")
                    .setMessage("Do you really want to add this person to contacts?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
						public void onClick(DialogInterface dialog, int whichButton) {
                            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                            int rawContactInsertIndex = ops.size();


                            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
                            ops.add(ContentProviderOperation
                                    .newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactData[0]) // Name of the person
                                    .build());
                            ops.add(ContentProviderOperation
                                    .newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(
                                            ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactData[1]) // Number of the person
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); // Type of mobile number
                            try {
                                ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

                                message.print("Contact added successfully!");
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch (OperationApplicationException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
        else {
            message.print("Number already in contacts!");
        }

        return true;
    }

    private boolean isInContacts(String incommingNumber) {
        Cursor cursor =null;
        String name = null;
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incommingNumber));
            cursor = this.getContentResolver().query(uri, new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }

        return name != null;
    }
}

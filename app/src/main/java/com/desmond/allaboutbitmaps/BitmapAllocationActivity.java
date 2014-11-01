package com.desmond.allaboutbitmaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This example shows how to speed up bitmap loading and reduce garbage collection
 * by reusing existing bitmaps.
 */
public class BitmapAllocationActivity extends ActionBarActivity {

    // There are some assumptions in this demo app that don't carry over well to the real world:
    // it assumes that all bitmaps are the same size and that loading all bitmaps as the activity
    // starts is good enough. A real application would be take a more flexible and robust
    // approach. But these assumptions are good enough for the purposes of this tutorial,
    // which is about reusing existing bitmaps of the same size.

    int mCurrentIndex = 0;
    Bitmap mCurrentBitmap = null;
    BitmapFactory.Options mBitmapOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_allocation);

        final int[] imageIDs = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d,
                R.drawable.e, R.drawable.f};

        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox);
        final TextView durationTextview = (TextView) findViewById(R.id.loadDuration);
        final ImageView imageview = (ImageView) findViewById(R.id.imageview);

        // Create bitmap to be re-used, based on the size of one of the bitmaps
        mBitmapOptions = new BitmapFactory.Options();
        mBitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.a, mBitmapOptions);
        mCurrentBitmap = Bitmap.createBitmap(mBitmapOptions.outWidth,
                mBitmapOptions.outHeight, Bitmap.Config.ARGB_8888);
        mBitmapOptions.inJustDecodeBounds = false;
        mBitmapOptions.inBitmap = mCurrentBitmap;
        mBitmapOptions.inSampleSize = 1;
        BitmapFactory.decodeResource(getResources(), R.drawable.a, mBitmapOptions);
        imageview.setImageBitmap(mCurrentBitmap);

        // When the user clicks on the image, load the next one in the list
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % imageIDs.length;
                BitmapFactory.Options bitmapOptions = null;

                if (checkbox.isChecked()) {
                    // Re-use the bitmap by using BitmapOptions.inBitmap
                    bitmapOptions = mBitmapOptions;
                    bitmapOptions.inBitmap = mCurrentBitmap;
                }
                long startTime = System.currentTimeMillis();
                mCurrentBitmap = BitmapFactory.decodeResource(getResources(), imageIDs[mCurrentIndex], bitmapOptions);
                imageview.setImageBitmap(mCurrentBitmap);

                // One way you can see the difference between reusing and not is through the
                // timing reported here. But you can also see a huge impact in the garbage
                // collector if you look at logcat with and without reuse. Avoiding garbage
                // collection when possible, especially for large items like bitmaps,
                // is always a good idea.
                durationTextview.setText("Load took " +
                        (System.currentTimeMillis() - startTime));
            }
        });
    }
}

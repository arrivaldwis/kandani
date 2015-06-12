package com.odt.kandani.View;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.odt.kandani.R;

/**
 * Created by ArrivalDwiS on 5/29/2015.
 */
public class ImageviewActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView imgView;
    private String mPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        imgView = (ImageView) findViewById(R.id.imageViewFull);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mPhoto);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle getType = getIntent().getExtras();
        mPhoto = getType.getString("photo");

        Bitmap bmp;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        bmp = BitmapFactory.decodeFile(mPhoto, options);
        imgView.setImageBitmap(bmp);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package com.odt.kandani.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.odt.kandani.R;

/**
 * Created by ArrivalDwiS on 5/29/2015.
 */
public class AboutActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mLicenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mLicenses = (TextView) findViewById(R.id.txtLicenses);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mLicenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), LicensesActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.pull_in_right,
                        R.anim.push_out_left);
            }
        });
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

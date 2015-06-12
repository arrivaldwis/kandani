package com.odt.kandani.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.odt.kandani.Components.AlarmReceiver;
import com.odt.kandani.R;
import com.odt.kandani.Database.Reminder;
import com.odt.kandani.Database.ReminderDatabase;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ReminderEditActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener{

    private Toolbar mToolbar;
    private ImageView imgCapture;
    private EditText mTitleText;
    private TextView mDateText, mTimeText, mRepeatText, mRepeatNoText, mRepeatTypeText;
    private FloatingActionButton mFAB1;
    private FloatingActionButton mFAB2, mRecord, mStop, mPlay;
    private Switch mRepeatSwitch;
    private String mTitle;
    private String mTime;
    private String mDate;
    private String mRepeatNo;
    private String mRepeatType;
    private String mVoice="";
    private String mPhoto="";
    private String mActive;
    private String mRepeat;
    private String[] mDateSplit;
    private String[] mTimeSplit;
    private int mReceivedID;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private Calendar mCalendar;
    private Reminder mReceivedReminder;
    private ReminderDatabase rb;
    private AlarmReceiver mAlarmReceiver;
    private String mType;
    private String judulSound = " ";
    private int status=0;
    private int voiceStatus=0;
    private Calendar kalender;
    Date sekarang = new Date();
    MediaPlayer mp = new MediaPlayer();
    // Sound
    private static final String AUDIO_RECORDER_FILE_EXT = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "Kandani Software";

    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,
            MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT };

    // Format Waktu
    public static final String formatWaktuTanggal = "yyyy-MM-dd kk:mm:ss";

    public static final String EXTRA_REMINDER_ID = "Reminder_ID";

    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_REPEAT = "repeat_key";
    private static final String KEY_REPEAT_NO = "repeat_no_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_VOICE = "voice_key";
    private static final String KEY_PHOTO = "photo_key";
    private static final String KEY_ACTIVE = "active_key";
    private static final String KEY_TYPE = "type_key";

    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    //Error Edittext
    int ecolor = 0xffffffff; // whatever color you want
    String estring = "Reminder Title cannot be blank!";
    ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
    SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        Bitmap bmp;
        ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
        kalender = Calendar.getInstance();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleText = (EditText) findViewById(R.id.reminder_title);
        mDateText = (TextView) findViewById(R.id.set_date);
        mTimeText = (TextView) findViewById(R.id.set_time);
        mRepeatText = (TextView) findViewById(R.id.set_repeat);
        imgCapture = (ImageView) findViewById(R.id.imgCapture);
        mRepeatNoText = (TextView) findViewById(R.id.set_repeat_no);
        mRepeatTypeText = (TextView) findViewById(R.id.set_repeat_type);
        mFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        mFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
        mRepeatSwitch = (Switch) findViewById(R.id.repeat_switch);
        mRecord = (FloatingActionButton) findViewById(R.id.record_voice);
        mStop = (FloatingActionButton) findViewById(R.id.stop_voice);
        mPlay = (FloatingActionButton) findViewById(R.id.play_voice);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_edit_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mReceivedID = Integer.parseInt(getIntent().getStringExtra(EXTRA_REMINDER_ID));

        rb = new ReminderDatabase(this);
        mReceivedReminder = rb.getReminder(mReceivedID);

        mTitle = mReceivedReminder.getTitle();
        mDate = mReceivedReminder.getDate();
        mTime = mReceivedReminder.getTime();
        mRepeat = mReceivedReminder.getRepeat();
        mRepeatNo = mReceivedReminder.getRepeatNo();
        mRepeatType = mReceivedReminder.getRepeatType();
        mActive = mReceivedReminder.getActive();
        mType = mReceivedReminder.getType();

        if(mType.equals("Text")) {
            status=1;
            imgCapture.setVisibility(View.GONE);
            mRecord.setVisibility(View.GONE);
            mStop.setVisibility(View.GONE);
            mPlay.setVisibility(View.GONE);
        } else if(mType.equals("Voice")) {
            status=2;
            mVoice = mReceivedReminder.getVoice();
            imgCapture.setVisibility(View.GONE);

            if(mVoice.isEmpty()) {
                voiceStatus=0;
                mRecord.setVisibility(View.VISIBLE);
                mStop.setVisibility(View.GONE);
                mPlay.setVisibility(View.GONE);
            } else {
                voiceStatus=1;
                mRecord.setVisibility(View.GONE);
                mStop.setVisibility(View.GONE);
                mPlay.setVisibility(View.VISIBLE);
            }
        } else if(mType.equals("Capture")) {
            status=3;
            mRecord.setVisibility(View.GONE);
            mStop.setVisibility(View.GONE);
            mPlay.setVisibility(View.GONE);
            mPhoto = mReceivedReminder.getPhoto();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;

            bmp = BitmapFactory.decodeFile(mPhoto,options);
            imgCapture.setImageBitmap(bmp);
            imgCapture.setVisibility(View.VISIBLE);
        }

        mTitleText.setText(mTitle);
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
        mRepeatNoText.setText(mRepeatNo);
        mRepeatTypeText.setText(mRepeatType);
        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");

        imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ImageviewActivity.class);
                i.putExtra("photo", mPhoto);
                startActivity(i);
            }
        });

        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle = savedTitle;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTime);
            mTime = savedTime;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate = savedDate;

            String saveRepeat = savedInstanceState.getString(KEY_REPEAT);
            mRepeatText.setText(saveRepeat);
            mRepeat = saveRepeat;

            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);
            mRepeatNoText.setText(savedRepeatNo);
            mRepeatNo = savedRepeatNo;

            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            mRepeatTypeText.setText(savedRepeatType);
            mRepeatType = savedRepeatType;

            mActive = savedInstanceState.getString(KEY_ACTIVE);
        }

        if (mActive.equals("false")) {
            mFAB1.setVisibility(View.VISIBLE);
            mFAB2.setVisibility(View.GONE);

        } else if (mActive.equals("true")) {
            mFAB1.setVisibility(View.GONE);
            mFAB2.setVisibility(View.VISIBLE);
        }

        if (mRepeat.equals("false")) {
            mRepeatSwitch.setChecked(false);
            mRepeatText.setText(R.string.repeat_off);

        } else if (mRepeat.equals("true")) {
            mRepeatSwitch.setChecked(true);
        }

        mCalendar = Calendar.getInstance();
        mAlarmReceiver = new AlarmReceiver();

        mDateSplit = mDate.split("/");
        mTimeSplit = mTime.split(":");

        mDay = Integer.parseInt(mDateSplit[0]);
        mMonth = Integer.parseInt(mDateSplit[1]);
        mYear = Integer.parseInt(mDateSplit[2]);
        mHour = Integer.parseInt(mTimeSplit[0]);
        mMinute = Integer.parseInt(mTimeSplit[1]);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
        outState.putCharSequence(KEY_REPEAT, mRepeatText.getText());
        outState.putCharSequence(KEY_REPEAT_NO, mRepeatNoText.getText());
        outState.putCharSequence(KEY_REPEAT_TYPE, mRepeatTypeText.getText());
        outState.putCharSequence(KEY_VOICE, mVoice);
        outState.putCharSequence(KEY_PHOTO, mPhoto);
        outState.putCharSequence(KEY_ACTIVE, mActive);
        outState.putCharSequence(KEY_TYPE, mType);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void setTime(View v){
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    public void setDate(View v){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        if (minute < 10) {
            mTime = hourOfDay + ":" + "0" + minute;
        } else {
            mTime = hourOfDay + ":" + minute;
        }
        mTimeText.setText(mTime);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear ++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
        mDate = dayOfMonth + "/" + monthOfYear + "/" + year;
        mDateText.setText(mDate);
    }

    public void selectFab1(View v) {
        mFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        mFAB1.setVisibility(View.GONE);
        mFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
        mFAB2.setVisibility(View.VISIBLE);
        mActive = "true";
    }

    public void selectFab2(View v) {
        mFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
        mFAB2.setVisibility(View.GONE);
        mFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        mFAB1.setVisibility(View.VISIBLE);
        mActive = "false";
    }

    public void selectRecord(View v) {
        mRecord = (FloatingActionButton) findViewById(R.id.record_voice);
        mRecord.setVisibility(View.GONE);
        mStop = (FloatingActionButton) findViewById(R.id.stop_voice);
        mStop.setVisibility(View.VISIBLE);
        Toast.makeText(ReminderEditActivity.this, "Recording..",
                Toast.LENGTH_SHORT).show();
        startRecording();
    }

    public void selectStop(View v) {
        mStop = (FloatingActionButton) findViewById(R.id.stop_voice);
        mStop.setVisibility(View.GONE);
        mRecord = (FloatingActionButton) findViewById(R.id.record_voice);
        mRecord.setVisibility(View.GONE);
        mPlay = (FloatingActionButton) findViewById(R.id.play_voice);
        mPlay.setVisibility(View.VISIBLE);
        Toast.makeText(ReminderEditActivity.this, "Saved..",
                Toast.LENGTH_SHORT).show();
        stopRecording();
    }

    public void playRecord(View v) {
        if (voiceStatus == 1) {
            mPlay.setVisibility(View.VISIBLE);
            mRecord.setVisibility(View.GONE);
            mStop.setVisibility(View.GONE);
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File("" + mVoice);
            //intent.setDataAndType(Uri.fromFile(file), "audio/*");
            //startActivity(intent);
            stopPlaying();
            mp = MediaPlayer.create(ReminderEditActivity.this, Uri.fromFile(file));
            mp.start();
            Toast.makeText(ReminderEditActivity.this, "Playing",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + mTitle + ","
                + kalender.get(Calendar.DAY_OF_MONTH) + "-" + kalender.DATE
                + "(" + kalender.get(Calendar.HOUR_OF_DAY) + "-"
                + sekarang.getMinutes() + ")" + file_exts[currentFormat]);

    }

    private void startRecording() {
        if (mTitleText.getText().toString().length() == 0)
            mTitleText.setError(ssbuilder);
        else {
            recorder = new MediaRecorder();
            mp = MediaPlayer.create(this, R.raw.anydo_pop);
            mp.start();

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(output_formats[currentFormat]);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(getFilename());

            recorder.setOnErrorListener(errorListener);
            recorder.setOnInfoListener(infoListener);

            mPlay.setVisibility(View.GONE);
            mRecord.setVisibility(View.GONE);
            mStop.setVisibility(View.VISIBLE);

            try {
                recorder.prepare();
                recorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(ReminderEditActivity.this, "Recording..",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.reset();
            recorder.release();

            mp = MediaPlayer.create(this, R.raw.anydo_moment_done);
            mp.start();

            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
                    formatWaktuTanggal);
            String reminderDateTime = dateTimeFormat.format(kalender.getTime());

            judulSound = (mTitle + "," + reminderDateTime + file_exts[currentFormat]);
            recorder = null;

            voiceStatus = 1;
            mVoice = getFilename().toString();
            Toast.makeText(ReminderEditActivity.this,
                    "Saved..", Toast.LENGTH_SHORT).show();
        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(ReminderEditActivity.this,
                    "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(ReminderEditActivity.this,
                    "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");

        } else {
            mRepeat = "false";
            mRepeatText.setText(R.string.repeat_off);
        }
    }

    public void selectRepeatType(View v){
        final String[] items = new String[5];

        items[0] = "Minute";
        items[1] = "Hour";
        items[2] = "Day";
        items[3] = "Week";
        items[4] = "Month";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                mRepeatType = items[item];
                mRepeatTypeText.setText(mRepeatType);
                mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setRepeatNo(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().length() == 0) {
                            mRepeatNo = Integer.toString(1);
                            mRepeatNoText.setText(mRepeatNo);
                            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        }
                        else {
                            mRepeatNo = input.getText().toString().trim();
                            mRepeatNoText.setText(mRepeatNo);
                            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        }
                    }
                });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    public void updateReminder(){
        mReceivedReminder.setTitle(mTitle);
        mReceivedReminder.setDate(mDate);
        mReceivedReminder.setTime(mTime);
        mReceivedReminder.setRepeat(mRepeat);
        mReceivedReminder.setRepeatNo(mRepeatNo);
        mReceivedReminder.setRepeatType(mRepeatType);

        if(mType.equals("Voice")) {
            mReceivedReminder.setVoice(mVoice);
        } else {
            mReceivedReminder.setVoice("");
        }

        if(mType.equals("Capture")) {
            mReceivedReminder.setPhoto(mPhoto);
        } else {
            mReceivedReminder.setPhoto("");
        }

        mReceivedReminder.setActive(mActive);
        mReceivedReminder.setType(mType);

        rb.updateReminder(mReceivedReminder);
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        mAlarmReceiver.cancelAlarm(getApplicationContext(), mReceivedID);

        if (mRepeatType.equals("Minute")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
        } else if (mRepeatType.equals("Hour")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
        } else if (mRepeatType.equals("Day")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
        } else if (mRepeatType.equals("Week")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
        } else if (mRepeatType.equals("Month")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
        }

        if (mActive.equals("true")) {
            if (mRepeat.equals("true")) {
                mAlarmReceiver.setRepeatAlarm(getApplicationContext(), mCalendar, mReceivedID, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                mAlarmReceiver.setAlarm(getApplicationContext(), mCalendar, mReceivedID);
            }
        }

        Toast.makeText(getApplicationContext(), "Edited",
                Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.save_reminder:
                mTitleText.setText(mTitle);

                if (mTitleText.getText().toString().length() == 0)
                    mTitleText.setError(ssbuilder);
                else {
                    updateReminder();
                }
                return true;

            case R.id.discard_reminder:
                Toast.makeText(getApplicationContext(), "Changes Discarded",
                        Toast.LENGTH_SHORT).show();

                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
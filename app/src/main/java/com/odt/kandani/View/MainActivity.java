package com.odt.kandani.View;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.odt.kandani.Components.AlarmReceiver;
import com.odt.kandani.Components.DateTimeSorter;
import com.odt.kandani.R;
import com.odt.kandani.Database.Reminder;
import com.odt.kandani.Database.ReminderDatabase;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        com.github.amlcurran.showcaseview.OnShowcaseEventListener {
    private RecyclerView mList;
    private SimpleAdapter mAdapter;
    private Toolbar mToolbar;
    private TextView mNoReminderView;
    private ImageView mIcadd;
    private FloatingActionButton mAddReminderButton;
    private FloatingActionButton mAddReminderButtonVoice;
    private FloatingActionButton mAddReminderButtonCapture;
    private FloatingActionsMenu mMultipleActions;
    private int mTempPost;
    private LinkedHashMap<Integer, Integer> IDmap = new LinkedHashMap<>();
    private ReminderDatabase rb;
    private MultiSelector mMultiSelector = new MultiSelector();
    private AlarmReceiver mAlarmReceiver;
    private ShowcaseView sv;
    private SharedPreferences tutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        tutorial = getSharedPreferences("ActivityTUTOR", Context.MODE_PRIVATE);
        if (pref.getBoolean("activity_executed", true)) {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", false);
            ed.commit();

            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_main);

            rb = new ReminderDatabase(getApplicationContext());
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            mAddReminderButton = (FloatingActionButton) findViewById(R.id.add_reminder);
            mAddReminderButtonVoice = (FloatingActionButton) findViewById(R.id.add_reminder_voice);
            mAddReminderButtonCapture = (FloatingActionButton) findViewById(R.id.add_reminder_capture);
            mList = (RecyclerView) findViewById(R.id.reminder_list);
            mNoReminderView = (TextView) findViewById(R.id.no_reminder_text);
            mIcadd = (ImageView) findViewById(R.id.ic_add);
            mMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
            List<Reminder> mTest = rb.getAllReminders();

            if (mTest.isEmpty()) {
                mNoReminderView.setVisibility(View.VISIBLE);
                mIcadd.setVisibility(View.VISIBLE);
            }

            mList.setLayoutManager(getLayoutManager());
            registerForContextMenu(mList);
            mAdapter = new SimpleAdapter();
            mAdapter.setItemCount(getDefaultItemCount());
            mList.setAdapter(mAdapter);

            setSupportActionBar(mToolbar);
            mToolbar.setTitle(R.string.app_name);

            mAddReminderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMultipleActions.collapse();
                    Intent intent = new Intent(v.getContext(), ReminderAddActivity.class);
                    intent.putExtra("tipe", "Text");
                    startActivity(intent);
                }
            });

            mAddReminderButtonVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMultipleActions.collapse();
                    Intent intent = new Intent(v.getContext(), ReminderAddActivity.class);
                    intent.putExtra("tipe", "Voice");
                    startActivity(intent);
                }
            });

            mAddReminderButtonCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMultipleActions.collapse();
                    Intent intent = new Intent(v.getContext(), CameraActivity.class);
                    startActivity(intent);
                }
            });

            mAlarmReceiver = new AlarmReceiver();

            if (tutorial.getBoolean("activity_executed", true)) {
                RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                int margin = ((Number) (getResources().getDisplayMetrics().density * 65)).intValue();
                lps.setMargins(30, margin, margin, margin);

                ViewTarget target = new ViewTarget(R.id.btn_tutor1, this);
                sv = new ShowcaseView.Builder(this, true)
                        .setTarget(target)
                        .setContentTitle(R.string.title1)
                        .setContentText(R.string.message1)
                        .setStyle(R.style.CustomShowcaseTheme2)
                        .setShowcaseEventListener(this)
                        .build();
                sv.setButtonPosition(lps);
                if (sv.isShown()) {
                    sv.setStyle(R.style.CustomShowcaseTheme2);
                } else {
                    sv.show();
                }
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_long, menu);
    }

    private android.support.v7.view.ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_long, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.discard_reminder:
                    actionMode.finish();
                    for (int i = IDmap.size(); i >= 0; i--) {
                        if (mMultiSelector.isSelected(i, 0)) {
                            int id = IDmap.get(i);
                            Reminder temp = rb.getReminder(id);
                            String type = temp.getType();

                            if(type.equals("Voice")) {
                                String voice = temp.getVoice();
                                File file = new File(voice);
                                file.delete();
                            } else if(type.equals("Capture")) {
                                String capture = temp.getPhoto();
                                File file = new File(capture);
                                file.delete();
                            }

                            rb.deleteReminder(temp);
                            mAdapter.removeItemSelected(i);
                            mAlarmReceiver.cancelAlarm(getApplicationContext(), id);

                        }
                    }

                    mMultiSelector.clearSelections();
                    mAdapter.onDeleteItem(getDefaultItemCount());
                    Toast.makeText(getApplicationContext(),
                            "Deleted",
                            Toast.LENGTH_SHORT).show();

                    List<Reminder> mTest = rb.getAllReminders();

                    if (mTest.isEmpty()) {
                        mNoReminderView.setVisibility(View.VISIBLE);
                        mIcadd.setVisibility(View.VISIBLE);
                    } else {
                        mNoReminderView.setVisibility(View.GONE);
                        mIcadd.setVisibility(View.GONE);
                    }

                    return true;

                case R.id.save_reminder:
                    actionMode.finish();
                    mMultiSelector.clearSelections();
                    return true;

                default:
                    break;
            }
            return false;
        }
    };

    private void selectReminder(int mClickID) {
        String mStringClickID = Integer.toString(mClickID);
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.setItemCount(getDefaultItemCount());
    }

    @Override
    public void onResume(){
        super.onResume();

        List<Reminder> mTest = rb.getAllReminders();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
            mIcadd.setVisibility(View.VISIBLE);
        } else {
            mNoReminderView.setVisibility(View.GONE);
            mIcadd.setVisibility(View.GONE);
        }

        mAdapter.setItemCount(getDefaultItemCount());
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    protected int getDefaultItemCount() {
        return 100;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;

            case R.id.rate:
                Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        mMultipleActions.expand();

        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 65)).intValue();
        lps.setMargins(30, margin, margin, margin);

        ViewTarget target = new ViewTarget(R.id.multiple_actions, this);
        sv = new ShowcaseView.Builder(this, true)
                .setTarget(target)
                .setContentTitle(R.string.title2)
                .setContentText(R.string.message2)
                .setStyle(R.style.CustomShowcaseTheme)
                .build();
        sv.setButtonPosition(lps);
        if (sv.isShown()) {
            sv.setStyle(R.style.CustomShowcaseTheme);
        } else {
            sv.show();
        }

        SharedPreferences.Editor ed = tutorial.edit();
        ed.putBoolean("activity_executed", false);
        ed.commit();
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }

    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VerticalItemHolder> {
        private ArrayList<ReminderItem> mItems;

        public SimpleAdapter() {
            mItems = new ArrayList<>();
        }

        public void setItemCount(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
            notifyDataSetChanged();
        }

        public void onDeleteItem(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
        }

        public void removeItemSelected(int selected) {
            if (mItems.isEmpty()) return;
            mItems.remove(selected);
            notifyItemRemoved(selected);
        }

        @Override
        public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View root = inflater.inflate(R.layout.recycle_items, container, false);

            return new VerticalItemHolder(root, this);
        }

        @Override
        public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {
            ReminderItem item = mItems.get(position);
            itemHolder.setReminderTitle(item.mTitle);
            itemHolder.setReminderDateTime(item.mDateTime);
            itemHolder.setReminderRepeatInfo(item.mRepeat, item.mRepeatNo, item.mRepeatType);
            itemHolder.setReminderType(item.mType);
            itemHolder.setActiveImage(item.mActive);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public  class ReminderItem {
            public String mTitle;
            public String mDateTime;
            public String mRepeat;
            public String mRepeatNo;
            public String mRepeatType;
            public String mVoice;
            public String mActive;
            public String mType;

            public ReminderItem(String Title, String DateTime, String Repeat, String RepeatNo, String RepeatType,String Voice, String Active, String Type) {
                this.mTitle = Title;
                this.mDateTime = DateTime;
                this.mRepeat = Repeat;
                this.mRepeatNo = RepeatNo;
                this.mRepeatType = RepeatType;
                this.mVoice = Voice;
                this.mActive = Active;
                this.mType = Type;
            }
        }

        public class DateTimeComparator implements Comparator {
            DateFormat f = new SimpleDateFormat("dd/mm/yyyy hh:mm");

            public int compare(Object a, Object b) {
                String o1 = ((DateTimeSorter)a).getDateTime();
                String o2 = ((DateTimeSorter)b).getDateTime();

                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        public  class VerticalItemHolder extends SwappingHolder
                implements View.OnClickListener, View.OnLongClickListener {
            private TextView mTitleText, mDateAndTimeText, mRepeatInfoText, mTitleType;
            private ImageView mActiveImage , mThumbnailImage;
            private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
            private TextDrawable mDrawableBuilder;
            private SimpleAdapter mAdapter;

            public VerticalItemHolder(View itemView, SimpleAdapter adapter) {
                super(itemView, mMultiSelector);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                itemView.setLongClickable(true);

                mAdapter = adapter;
                mTitleText = (TextView) itemView.findViewById(R.id.recycle_title);
                mTitleType = (TextView) itemView.findViewById(R.id.type_info);
                mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time);
                mRepeatInfoText = (TextView) itemView.findViewById(R.id.recycle_repeat_info);
                mActiveImage = (ImageView) itemView.findViewById(R.id.active_image);
                mThumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            }

            @Override
            public void onClick(View v) {
                if (!mMultiSelector.tapSelection(this)) {
                    mTempPost = mList.getChildAdapterPosition(v);

                    int mReminderClickID = IDmap.get(mTempPost);
                    selectReminder(mReminderClickID);

                } else if(mMultiSelector.getSelectedPositions().isEmpty()){
                    mAdapter.setItemCount(getDefaultItemCount());
                }
            }

            @Override
            public boolean onLongClick(View v) {
                AppCompatActivity activity = MainActivity.this;
                activity.startSupportActionMode(mDeleteMode);
                mMultiSelector.setSelected(this, true);
                return true;
            }

            public void setReminderTitle(String title) {
                mTitleText.setText(title);
                String letter = "A";

                if(title != null && !title.isEmpty()) {
                    letter = title.substring(0, 1);
                }

                int color = mColorGenerator.getRandomColor();
                mDrawableBuilder = TextDrawable.builder()
                        .buildRound(letter, color);
                mThumbnailImage.setImageDrawable(mDrawableBuilder);
            }

            public void setReminderType(String type) {
                if (type.equals("Text")) {
                    mTitleType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit45_w, 0, 0, 0);
                } else {
                    mTitleType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_voice29_w, 0, 0, 0);
                }
            }

            public void setReminderDateTime(String datetime) {
                mDateAndTimeText.setText(" "+datetime);
            }

            public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
                if(repeat.equals("true")){
                    mRepeatInfoText.setText(repeatNo + " " + repeatType + "(s)");
                }else if (repeat.equals("false")) {
                    mRepeatInfoText.setText("Repeat Off");
                }
            }

            public void setActiveImage(String active){
                if(active.equals("true")){
                    mActiveImage.setImageResource(R.drawable.ic_notifications_on_grey600_24dp);
                }else if (active.equals("false")) {
                    mActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);
                }
            }
        }

        //testing data dami
        public  ReminderItem generateDummyData() {
            return new ReminderItem("1", "2", "3", "4", "5", "6", "7", "8");
        }

        public List<ReminderItem> generateData(int count) {
            ArrayList<SimpleAdapter.ReminderItem> items = new ArrayList<>();

            List<Reminder> reminders = rb.getAllReminders();
            List<String> Titles = new ArrayList<>();
            List<String> Repeats = new ArrayList<>();
            List<String> RepeatNos = new ArrayList<>();
            List<String> RepeatTypes = new ArrayList<>();
            List<String> Voice = new ArrayList<>();
            List<String> Actives = new ArrayList<>();
            List<String> DateAndTime = new ArrayList<>();
            List<String> Type = new ArrayList<>();
            List<Integer> IDList= new ArrayList<>();
            List<DateTimeSorter> DateTimeSortList = new ArrayList<>();

            for (Reminder r : reminders) {
                Titles.add(r.getTitle());
                DateAndTime.add(r.getDate() + " " + r.getTime());
                Repeats.add(r.getRepeat());
                RepeatNos.add(r.getRepeatNo());
                RepeatTypes.add(r.getRepeatType());
                Voice.add(r.getVoice());
                Actives.add(r.getActive());
                Type.add(r.getType());
                IDList.add(r.getID());
            }

            int key = 0;
            for(int k = 0; k<Titles.size(); k++){
                DateTimeSortList.add(new DateTimeSorter(key, DateAndTime.get(k)));
                key++;
            }

            Collections.sort(DateTimeSortList, new DateTimeComparator());

            int k = 0;

            for (DateTimeSorter item:DateTimeSortList) {
                int i = item.getIndex();

                items.add(new SimpleAdapter.ReminderItem(Titles.get(i), DateAndTime.get(i), Repeats.get(i),
                        RepeatNos.get(i), RepeatTypes.get(i),Voice.get(i), Actives.get(i), Type.get(i)));
                IDmap.put(k, IDList.get(i));
                k++;
            }
          return items;
        }
    }
}

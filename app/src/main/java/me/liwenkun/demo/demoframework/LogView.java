package me.liwenkun.demo.demoframework;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.liwenkun.demo.R;

public class LogView extends FrameLayout {

    private ListView lvLogs;
    private boolean interactionWithLogs = false;
    private boolean reachEnd = true;

    private final SimpleDateFormat simpleDateFormat
            = new SimpleDateFormat("[HH:mm:ss.SSS] ", Locale.getDefault());


    private final List<LogItem> logs = new ArrayList<>();
    private ArrayAdapter<LogItem> logAdapter;

    List<String> tags = new ArrayList<>(){{
        add("默认");
    }};
    String selectedTag = "";
    private ArrayAdapter<String> tagAdapter;

    private static class LogItem {
        private SpannableStringBuilder styledText;
        private String promptChar;
        private String tag;
        private String msg;
        @ColorInt
        private int color;
        private String dateString;

        private CharSequence getStyledText() {
            if (styledText == null) {
                styledText = new SpannableStringBuilder()
                        .append(dateString)
                        .append(promptChar == null ? "" : promptChar,
                                new ForegroundColorSpan(Color.GREEN),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (!TextUtils.isEmpty(tag)) {
                    styledText.append(tag, new BackgroundColorSpan(Color.GREEN & 0x55FFFFFF),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                            .append(" ");
                }
                if (!TextUtils.isEmpty(msg)) {
                    styledText.append(msg);
                }
            }
            return styledText;
        }

        @NotNull
        @Override
        public String toString() {
            return tag + msg;
        }
    }

    public LogView(@NonNull Context context) {
        this(context, null);
    }

    public LogView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LogView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_log_view, this);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        lvLogs = findViewById(R.id.logs);
        View handle = findViewById(R.id.log_view_toolbar);
        handle.setOnTouchListener(new OnTouchListener() {
            float downY;
            int originHeight;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                    offset(event.getRawY());
                } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    v.setActivated(false);
                } else if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    downY = event.getRawY();
                    originHeight = getHeight();
                    v.setActivated(true);
                }
                return false;
            }

            private void offset(float y) {
                ViewGroup.LayoutParams lp = getLayoutParams();
                lp.height = Math.min(Math.max(originHeight - (int)(y - downY), handle.getHeight()),
                        ((ViewGroup) getParent()).getHeight());
                requestLayout();
            }
        });
        logAdapter = new ArrayAdapter<>(getContext(),
                R.layout.layout_log_item, R.id.log, logs) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                LogItem logItem = getItem(position);
                if (logItem != null) {
                    TextView tv = view.findViewById(R.id.log);
                    tv.setTextColor(logItem.color);
                    tv.setText(logItem.getStyledText());
                }
                return view;
            }
        };
        lvLogs.setAdapter(logAdapter);
        lvLogs.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);


        lvLogs.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                interactionWithLogs = true;
                lvLogs.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
            } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                interactionWithLogs = false;
                if (reachEnd) {
                    lvLogs.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                }
            }
            return false;
        });

        lvLogs.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    View lastVisibleItemView = view.getChildAt(view.getChildCount() - 1);
                    reachEnd = lastVisibleItemView != null
                            && lastVisibleItemView.getBottom() + view.getPaddingBottom() == view.getHeight();
                } else {
                    reachEnd = false;
                }
                if (reachEnd && !interactionWithLogs) {
                    view.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                }
            }
        });

        Toolbar logViewToolbar = findViewById(R.id.log_view_toolbar);
        Spinner spinner = new Spinner(getContext());
        spinner.setGravity(Gravity.END);
        tagAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                new ArrayList<>(tags)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                return view;
            }
        };
        spinner.setAdapter(tagAdapter);
        logViewToolbar.getMenu().add(Menu.NONE, 1, 0,
                        spinner.getSelectedItem().toString())
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setActionView(spinner);
        logViewToolbar.getMenu().add(Menu.NONE, 2, 1,
                        "清除日志")
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
        logViewToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 2) {
                deleteAllLogs();
                return true;
            }
            return false;
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTag = position == 0 ? "" : tags.get(position);
                logAdapter.getFilter().filter(selectedTag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void deleteAllLogs() {
        logAdapter.clear();
        logAdapter.getFilter().filter(selectedTag);
    }

    public void print(String tag, String msg, int color, String promptChar) {
        LogItem log = new LogItem();
        log.dateString = simpleDateFormat.format(new Date());
        log.promptChar = promptChar;
        log.msg = msg;
        log.color = color;
        log.tag = tag;
        logAdapter.add(log);
        logAdapter.getFilter().filter(selectedTag);

        if (!TextUtils.isEmpty(tag) && !tags.contains(tag)) {
            tags.add(tag);
            tagAdapter.add(tag);
        }
    }

    public void scrollToTop() {
        lvLogs.setSelection(0);
    }

    public void scrollToBottom() {
        lvLogs.setSelection(logAdapter.getCount() - 1);
    }
}

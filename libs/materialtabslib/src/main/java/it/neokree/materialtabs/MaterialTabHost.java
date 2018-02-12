package it.neokree.materialtabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.List;


/**
 * A Toolbar that contains multiple tabs
 * @author neokree
 *
 */
@SuppressLint("InflateParams")
public class MaterialTabHost extends RelativeLayout{

    private LayoutInflater mInflater;

    private int mPrimaryColor;
    private int mSelectorColor;
    private int mItemTabLayoutRes;
    private int mItemTabLayoutBackground;
    private List<MaterialTab> mTabs;
    private boolean isTablet;
    private float mDensity;
    private boolean mScrollable;

//    private DisplayMetrics displayMetrics;

    private HorizontalScrollView scrollView;
    private LinearLayout layout;
//    private ImageButton left;
//    private ImageButton right;

    public MaterialTabHost(Context context) {
        this(context, null);
    }

    public MaterialTabHost(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialTabHost(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        scrollView = new HorizontalScrollView(context);
        scrollView.setOverScrollMode(HorizontalScrollView.OVER_SCROLL_NEVER);
        scrollView.setHorizontalScrollBarEnabled(false);
        layout = new LinearLayout(context);
        scrollView.addView(layout);

        //displayMetrics = context.getResources().getDisplayMetrics();

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // get attributes
        if(attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MaterialTabHost, 0, 0);
            try {
                // custom attributes
                mPrimaryColor = a.getColor(R.styleable.MaterialTabHost_tabColor, Color.TRANSPARENT);
                mSelectorColor = a.getColor(R.styleable.MaterialTabHost_accentTabColor,Color.parseColor("#ff80cbc4"));
                mItemTabLayoutRes = a.getResourceId(R.styleable.MaterialTabHost_itemTabLayout, R.layout.custom_material_tab_icon);
                mItemTabLayoutBackground = a.getResourceId(R.styleable.MaterialTabHost_itemTabBackground, R.drawable.custom_selector);
            } finally {
                a.recycle();
            }
        }

        this.isInEditMode();
        mScrollable = false;
        final Resources resources = context.getApplicationContext().getResources();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        final Configuration configuration = resources.getConfiguration();
        //isTablet = this.getResources().getBoolean(R.bool.isTablet);
        isTablet = isTabletDevice(configuration.screenLayout);
        mDensity = displayMetrics.density;

        // initialize tabs list
        mTabs = new LinkedList<MaterialTab>();


        // set background color
        setBackgroundColor(mPrimaryColor);
    }

    public void setSelectorsColor(int color) {
        this.mSelectorColor = color;

        for(MaterialTab tab : mTabs) {
            tab.setSelectorColor(color);
        }
    }

    public int getTabCount(){
        return mTabs.size();
    }

    public void addTab(MaterialTab tab) {
        // add properties to tab
        tab.setSelectorColor(mSelectorColor);
        tab.setPosition(mTabs.size());

        // insert new tab in list
        mTabs.add(tab);

        if(mTabs.size() >= 5) {
            // switch tabs to mScrollable before its draw
            mScrollable = true;
        }
    }

    public MaterialTab newTab() {
        View tabLayout = mInflater.inflate(mItemTabLayoutRes, null);
        tabLayout.setBackgroundResource(mItemTabLayoutBackground);
        return new MaterialTab(tabLayout, mDensity);
    }

    public void setSelectedNavigationItem(int position){
        if(position < 0 || position > mTabs.size()) {
            throw new RuntimeException("Index overflow");
        } else {
            // tab at position will select, other will deselect
            for(int i = 0; i < mTabs.size(); i++) {
                MaterialTab tab = mTabs.get(i);

                if(i == position) {
                    tab.activateTab();
                }
                else {
                    mTabs.get(i).disableTab();
                }
            }

            // move the tab if it is slidable
            if(mScrollable) {
                int totalWidth = 0;//(int) ( 60 * mDensity);
                for (int i = 0; i < position; i++) {
                    totalWidth += mTabs.get(i).getView().getWidth();
                }
                scrollView.smoothScrollTo(totalWidth, 0);
            }
        }
    }

    public int getSelectedNavigationIndex() {
        int position=0;
        for(MaterialTab tab : mTabs) {
            if(tab.isSelected()) {
                position = tab.getPosition();
            }
        }
        return position;
    }


    @Override
    public void removeAllViews() {
        mTabs.clear();
        layout.removeAllViews();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(this.getWidth() != 0 && mTabs.size() != 0){
            notifyDataSetChanged();
        }
    }


    public void notifyDataSetChanged() {
        super.removeAllViews();
        layout.removeAllViews();

        if (!mScrollable) { // not mScrollable tabs
            int tabWidth = this.getWidth() / mTabs.size();

            // set params for resizing tabs width
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tabWidth, HorizontalScrollView.LayoutParams.MATCH_PARENT);
            for (MaterialTab t : mTabs) {
                layout.addView(t.getView(), params);
            }

        } else { //mScrollable tabs

            if(!isTablet) {
                for (int i = 0; i < mTabs.size(); i++) {
                    LinearLayout.LayoutParams params;
                    MaterialTab tab = mTabs.get(i);

                    int tabWidth = (int) (tab.getTabMinWidth() + (24 * mDensity)); // 12dp + text/icon width + 12dp

                    if (i == 0) {
                        // first tab
                        View view = new View(this.getContext());
                        view.setMinimumWidth((int) (60 * mDensity));
                        layout.addView(view);
                    }

                    params = new LinearLayout.LayoutParams(tabWidth, HorizontalScrollView.LayoutParams.MATCH_PARENT);
                    layout.addView(tab.getView(), params);

                    if (i == mTabs.size() - 1) {
                        // last tab
                        View view = new View(this.getContext());
                        view.setMinimumWidth((int) (60 * mDensity));
                        layout.addView(view);
                    }
                }
            }
            else {
                // is a tablet
                for (MaterialTab mTab : mTabs) {
                    LinearLayout.LayoutParams params;
                    //MaterialTab tab = mTab;

                    int tabWidth = (int) (mTab.getTabMinWidth() + (48 * mDensity)); // 24dp + text/icon width + 24dp

                    params = new LinearLayout.LayoutParams(tabWidth, HorizontalScrollView.LayoutParams.MATCH_PARENT);
                    layout.addView(mTab.getView(), params);
                }
            }
        }

        // if is not a tablet add only mScrollable content
        final RelativeLayout.LayoutParams paramsScroll = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        super.post(new Runnable() {
            @Override
            public void run() {
                addView(scrollView, paramsScroll);
                requestLayout();
            }
        });
    }

    public MaterialTab getCurrentTab()
    {
        for(MaterialTab tab : mTabs) {
            if (tab.isSelected())
                return tab;
        }
        return null;
    }

    /**
     * It detects if the current device is considered a tablet or not
     *
     * @param screenLayout App resources
     * @return true if it's a tablet device
     */
    private boolean isTabletDevice(int screenLayout) {
        switch (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return true;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return false;
            default:
                return true;
        }
    }
}

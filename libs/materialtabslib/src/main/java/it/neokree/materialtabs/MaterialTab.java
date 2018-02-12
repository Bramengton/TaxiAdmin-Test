package it.neokree.materialtabs;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
/**
 * A simple Tab with Material Design style
 * @author neokree
 *
 */ 
public class MaterialTab {

    private View mCompleteView;
	private ImageView mIcon;
	private TextView mLabel;
	private ImageView mSelector;

	private MaterialTabListener mListener;

	private int mSelectorColor;
    //private int mTransparentColor;

	private boolean mActive;
	private int mPosition;
    private float mDensity;


	MaterialTab(View view, float density) {
        mDensity = density;
        //mTransparentColor = resources.getColor(android.R.color.transparent);

		//mCompleteView = LayoutInflater.from(ctx).inflate(R.layout.custom_material_tab_icon, null);
		mIcon = (ImageView) view.findViewById(R.id.icon);
        mLabel = (TextView) view.findViewById(R.id.label);
		mSelector = (ImageView) view.findViewById(R.id.selector);

        //mCompleteView.setBackgroundResource(R.drawable.custom_selector);
        view.setOnClickListener(getClicked(this));
        mCompleteView = view;
		mActive = false;
	}

	public void setSelectorColor(int color) {
		this.mSelectorColor = color;
	}

	public MaterialTab setIcon(int icon) {
		this.mIcon.setImageResource(icon);
		return this;
	}

    public MaterialTab setLabel(int label) {
        this.mLabel.setText(label);
        return this;
    }
	
	public void disableTab() {
		// set transparent the selector view
		this.mSelector.setBackgroundColor(Color.TRANSPARENT);
		mActive = false;
		
		if(mListener != null)
			mListener.onTabUnselected(this);
	}
	
	public void activateTab() {
		// set accent color to selector view
		this.mSelector.setBackgroundColor(mSelectorColor);
		mActive = true;
	}
	
	public boolean isSelected() {
		return mActive;
	}

	public View getView() {
		return mCompleteView;
	}
	
	public MaterialTab setTabListener(MaterialTabListener listener) {
		this.mListener = listener;
		return this;
	}

    public MaterialTabListener getTabListener() {
        return mListener;
    }

	public int getPosition() {
		return mPosition;
	}

	public void setPosition(int position) {
		this.mPosition = position;
	}

    private int getIconWidth() {
        return (int) (mDensity * 24);
    }

    public int getTabMinWidth() {
        return getIconWidth();
    }

    private View.OnClickListener getClicked(final MaterialTab tab){
	    return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final MaterialTab tab = tab;
                Animator iconAnim = IconAnim();
                iconAnim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getClickEvent(tab);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
                iconAnim.start();
            }
        };
    }

//    @Override
//    public void onClick(View v){
//        final MaterialTab tab = this;
//        Animator iconAnim = IconAnim();
//        iconAnim.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {}
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                getClickEvent(tab);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {}
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {}
//        });
//        iconAnim.start();
//    }

    private Animator IconAnim() {
        return ObjectAnimator.ofPropertyValuesHolder(
                this.mIcon,
                PropertyValuesHolder.ofFloat("scaleX", 1f, 1.5f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f, 1.5f, 1f));
    }

    private void getClickEvent(MaterialTab tab){
        // set the click
        if(mListener != null) {

            if(mActive) {
                // if the tab is active when the user click on it it will be reselect
                mListener.onTabReselected(tab);
            }
            else {
                mListener.onTabSelected(tab);
            }
        }
        // if the tab is not activated, it will be active
        if(!mActive)
            tab.activateTab();
    }
}

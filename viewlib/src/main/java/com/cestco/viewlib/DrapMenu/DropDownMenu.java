package com.cestco.viewlib.DrapMenu;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cestco.viewlib.R;

import java.util.List;


/**
 * Created by Fussen on 16/8/31.
 * Change by 扬天 on 17/3/31.
 * change by wdy on 17/9/26
 */
public class DropDownMenu extends LinearLayout {

    //顶部菜单布局
    private LinearLayout tabMenuView;
    //底部容器，包含popupMenuViews，maskView
    private FrameLayout containerView;
    //弹出菜单父布局
    private FrameLayout popupMenuViews;
    //遮罩半透明View，点击可关闭DropDownMenu
    private View maskView;
    //tabMenuView里面选中的tab位置，-1表示未选中
    private int current_tab_position = -1;

    //分割线颜色
    private int dividerColor = 0xffcccccc;
    //tab选中颜色
    private int textSelectedColor = 0xff890c85;
    //tab未选中颜色
    private int textUnselectedColor = 0xff111111;
    //遮罩颜色
    private int maskColor = 0x88888888;
    //tab字体大小
    private int menuTextSize = 14;

    //tab选中图标
    private int menuSelectedIcon;
    //tab未选中图标
    private int menuUnselectedIcon;
    //
    private int tabViewWidth = LayoutParams.WRAP_CONTENT;
    private int textPaddingButtom = 0;
    private int textPaddingTop = 0;

    public DropDownMenu(Context context) {
        super(context, null);
    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);

        //为DropDownMenu添加自定义属性
        int menuBackgroundColor = 0x00000000;
        int underlineColor = 0xffcccccc;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
        underlineColor = a.getColor(R.styleable.DropDownMenu_underlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.DropDownMenu_dividerColor, dividerColor);
        textSelectedColor = a.getColor(R.styleable.DropDownMenu_textSelectedColor, textSelectedColor);
        textUnselectedColor = a.getColor(R.styleable.DropDownMenu_textUnselectedColor, textUnselectedColor);
        menuBackgroundColor = a.getColor(R.styleable.DropDownMenu_menuBackgroundColor, menuBackgroundColor);
        maskColor = a.getColor(R.styleable.DropDownMenu_maskColor, maskColor);
        menuTextSize = a.getDimensionPixelSize(R.styleable.DropDownMenu_menuTextSize, menuTextSize);
        menuSelectedIcon = a.getResourceId(R.styleable.DropDownMenu_menuSelectedIcon, menuSelectedIcon);
        menuUnselectedIcon = a.getResourceId(R.styleable.DropDownMenu_menuUnselectedIcon, menuUnselectedIcon);
        tabViewWidth = (int) a.getDimension(R.styleable.DropDownMenu_tabViewWidth ,tabViewWidth);
        textPaddingButtom = (int) a.getDimension(R.styleable.DropDownMenu_textPaddingBottom ,textPaddingButtom);
        textPaddingTop = (int) a.getDimension(R.styleable.DropDownMenu_textPaddingTop ,textPaddingTop);
        a.recycle();

        //初始化tabMenuView并添加到tabMenuView
        tabMenuView = new LinearLayout(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tabMenuView.setOrientation(HORIZONTAL);
        tabMenuView.setBackgroundColor(menuBackgroundColor);
        tabMenuView.setLayoutParams(params);
        addView(tabMenuView, 0);

        //为tabMenuView添加下划线
        View underLine = new View(getContext());
        underLine.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpTpPx(0.5f)));
        underLine.setBackgroundColor(underlineColor);
        addView(underLine, 1);

        //初始化containerView并将其添加到DropDownMenu
        containerView = new FrameLayout(context);
        containerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(containerView, 2);


    }

    /**
     * 初始化DropDownMenu
     *
     * @param tabTexts 值的顺序要和popupViews的值顺序相同
     * @param popupViews
     */
    public void setDropDownMenu(@NonNull List<String> tabTexts, @NonNull List<View> popupViews, Context context) {
        if (tabTexts.size() != popupViews.size()) {
            throw new IllegalArgumentException("params not match, tabTexts.size() should be equal popupViews.size()");
        }

        for (int i = 0; i < tabTexts.size(); i++) {
            addTab(tabTexts, i, context);
        }

        maskView = new View(getContext());
        maskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        maskView.setBackgroundColor(maskColor);
        maskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });
        containerView.addView(maskView, 0);
        maskView.setVisibility(GONE);

        popupMenuViews = new FrameLayout(getContext());
        popupMenuViews.setVisibility(GONE);
        containerView.addView(popupMenuViews, 1);

        for (int i = 0; i < popupViews.size(); i++) {
            popupViews.get(i).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            popupMenuViews.addView(popupViews.get(i), i);
        }

    }

    private void addTab(@NonNull List<String> tabTexts, int i, Context context) {
//        final TextView tab = new TextView(getContext());
//        tab.setSingleLine();
//        tab.setEllipsize(TextUtils.TruncateAt.END);
//        tab.setGravity(Gravity.CENTER);
//        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
//        tab.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
//        tab.setTextColor(textUnselectedColor);
//        tab.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(menuUnselectedIcon), null);
//        tab.setText(tabTexts.get(i));
//        tab.setPadding(dpTpPx(12), dpTpPx(12), dpTpPx(12), dpTpPx(12));
//        //添加点击事件
//        tab.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                switchMenu(tab);
//            }
//        });
//        tabMenuView.addView(tab);
        final View inflate = inflate(context, R.layout.item_drop_down, null);
        inflate.setLayoutParams(new ViewGroup.LayoutParams(tabViewWidth ,LayoutParams.WRAP_CONTENT));
        inflate.setPadding(0, dpTpPx(textPaddingTop), 0, dpTpPx(textPaddingButtom));
        TextView textView = (TextView) inflate.findViewById(R.id.tv_drop_down);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.iv_up_or_down);
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
        textView.setTextColor(textUnselectedColor);
        textView.setText(tabTexts.get(i));
//        textView.setPadding(0, dpTpPx(textPaddingTop), 0, dpTpPx(textPaddingButtom));
//        imageView.setPadding(0, dpTpPx(12), dpTpPx(12), dpTpPx(12));
//        ScreenFitUtils.auto(inflate);
        inflate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMenu(inflate);
            }
        });
        tabMenuView.addView(inflate);
        //添加分割线
        if (i < tabTexts.size() - 1) {
            View view = new View(getContext());
            LayoutParams layoutParams = new LayoutParams(dpTpPx(0.5f), ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            view.setLayoutParams(layoutParams);
            view.setBackgroundColor(dividerColor);
            tabMenuView.addView(view);
        }
    }

    private void changeMenu(View inflate) {
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {

            if (inflate == tabMenuView.getChildAt(i)) {
                if (current_tab_position == i) {
                    closeMenu();
                } else {
                    if (current_tab_position == -1) {
                        popupMenuViews.setVisibility(View.VISIBLE);
                        popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_in));
                        maskView.setVisibility(VISIBLE);
                        maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    } else {
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    }
                    current_tab_position = i;
                    View childAt = tabMenuView.getChildAt(i);
                    TextView textView = (TextView) childAt.findViewById(R.id.tv_drop_down);
                    ImageView imageView = (ImageView) childAt.findViewById(R.id.iv_up_or_down);
                    textView.setTextColor(textSelectedColor);
                    imageView.setImageDrawable(getResources().getDrawable(menuSelectedIcon));
                }
            } else {
                //设置其他未点击tab的状态
                View childAt = tabMenuView.getChildAt(i);
                TextView textView = (TextView) childAt.findViewById(R.id.tv_drop_down);
                ImageView imageView = (ImageView) childAt.findViewById(R.id.iv_up_or_down);
                textView.setTextColor(textUnselectedColor);
                imageView.setImageDrawable(getResources().getDrawable(menuUnselectedIcon));
                popupMenuViews.getChildAt(i / 2).setVisibility(View.GONE);
            }
        }
    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    public void setTabText(String text) {

        if (current_tab_position != -1) {
//            ((TextView) tabMenuView.getChildAt(current_tab_position)).setText(text);
            View childAt = tabMenuView.getChildAt(current_tab_position);
            TextView textView = (TextView) childAt.findViewById(R.id.tv_drop_down);
            textView.setText(text);
        }

    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    public void setTabText(String text ,int current_tab_position) {

        if (current_tab_position != -1) {
//            ((TextView) tabMenuView.getChildAt(current_tab_position)).setText(text);
            View childAt = tabMenuView.getChildAt(current_tab_position);
            TextView textView = (TextView) childAt.findViewById(R.id.tv_drop_down);
            textView.setText(text);
        }

    }

    public void setTabClickable(boolean clickable) {
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            tabMenuView.getChildAt(i).setClickable(clickable);
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (current_tab_position != -1) {
//            ((TextView) tabMenuView.getChildAt(current_tab_position)).setTextColor(textUnselectedColor);
//            ((TextView) tabMenuView.getChildAt(current_tab_position)).setCompoundDrawablesWithIntrinsicBounds(null, null,
//                    getResources().getDrawable(menuUnselectedIcon), null);
            View childAt = tabMenuView.getChildAt(current_tab_position);
            TextView textView = (TextView) childAt.findViewById(R.id.tv_drop_down);
            ImageView imageView = (ImageView) childAt.findViewById(R.id.iv_up_or_down);
            textView.setTextColor(textUnselectedColor);
            imageView.setImageDrawable(getResources().getDrawable(menuUnselectedIcon));
            popupMenuViews.setVisibility(View.GONE);
            popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_out));
            maskView.setVisibility(GONE);
            maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_out));
            current_tab_position = -1;
        }

    }

    /**
     * DropDownMenu是否处于可见状态
     *
     * @return
     */
    public boolean isShowing() {
        return current_tab_position != -1;
    }

    /**
     * 切换菜单
     *
     * @param view
     */
    private void switchMenu(View view) {

        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {

            if (view == tabMenuView.getChildAt(i)) {
                if (current_tab_position == i) {
                    closeMenu();
                } else {
                    if (current_tab_position == -1) {
                        popupMenuViews.setVisibility(View.VISIBLE);
                        popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_in));
                        maskView.setVisibility(VISIBLE);
                        maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    } else {
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    }
                    current_tab_position = i;
                    ((TextView) tabMenuView.getChildAt(i)).setTextColor(textSelectedColor);
                    ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                            getResources().getDrawable(menuSelectedIcon), null);
                }
            } else {
                //设置其他未点击tab的状态
                ((TextView) tabMenuView.getChildAt(i)).setTextColor(textUnselectedColor);
                ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(menuUnselectedIcon), null);
                popupMenuViews.getChildAt(i / 2).setVisibility(View.GONE);
            }
        }
    }

    public int dpTpPx(float value) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
    }

}
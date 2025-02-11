package io.agora.chatdemo.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public abstract class BaseDialogFragment extends DialogFragment {
    public BaseActivity mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (BaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArgument();
        View view = inflater.inflate(getLayoutId(), container, false);;
        setChildView(view);
        setDialogAttrs();
        return view;
    }

    public void setChildView(View view) {}

    public abstract int getLayoutId();

    private void setDialogAttrs() {
        try {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public void initArgument() {}

    public void initView(Bundle savedInstanceState) {}

    public void initListener() {}

    public void initData() {}

    /**
     * Should after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} to call
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    /**
     * Set width and height
     */
    public void setDialogParams() {
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.dimAmount = 0.6f;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity =  Gravity.BOTTOM;
            setDialogParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Full screen
     */
    public void setDialogFullParams() {
        int dialogHeight = getContextRect(mContext);
        int height = dialogHeight == 0? ViewGroup.LayoutParams.MATCH_PARENT:dialogHeight;
        setDialogParams(ViewGroup.LayoutParams.MATCH_PARENT, height, 0.0f);
    }

    public void setDialogParams(int width, int height, float dimAmount) {
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.dimAmount = dimAmount;
            lp.width = width;
            lp.height = height;
            dialogWindow.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDialogParams(WindowManager.LayoutParams layoutParams) {
        try {
            Window dialogWindow = getDialog().getWindow();
            dialogWindow.setAttributes(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getContextRect(Activity activity){
        Rect outRect1 = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        return outRect1.height();
    }

}

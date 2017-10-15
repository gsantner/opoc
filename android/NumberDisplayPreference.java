/*
* ------------------------------------------------------------------------------
* Gregor Santner <gsantner.github.io> wrote this. You can do whatever you want
* with it. If we meet some day, and you think it is worth it, you can buy me a
* coke in return. Provided as is without any kind of warranty. Do not blame or
* sue me if something goes wrong. No attribution required.    - Gregor Santner
*
* License: Creative Commons Zero (CC0 1.0)
*  http://creativecommons.org/publicdomain/zero/1.0/
* ----------------------------------------------------------------------------
*/
package net.gsantner.opoc.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import io.github.froodyapp.R;
import io.github.froodyapp.util.ContextUtils;

@SuppressWarnings({"unused", "WeakerAccess"})
public class NumberDisplayPreference extends Preference {
    protected int _textSize;
    protected int _currentValue;
    protected ImageView _icon;
    protected Bitmap _bitmap;

    public NumberDisplayPreference(Context context) {
        super(context);
    }

    public NumberDisplayPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberDisplayPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        _icon = (ImageView) view.findViewById(android.R.id.icon);
        setValue(_currentValue);
    }

    public int getValue() {
        return _currentValue;
    }

    public void setValue(int newValue) {
        setValue(newValue, 32);
    }

    public void setValue(int newValue, int textSize) {
        if (_bitmap == null || _currentValue != newValue) {
            _currentValue = newValue;
            _bitmap = ContextUtils.get().drawTextToBitmap(R.drawable.empty_64dp, String.valueOf(_currentValue), textSize);
        }

        if (_icon != null) {
            _icon.setImageBitmap(_bitmap);
        }
        _textSize = textSize;
    }

    @Override
    protected void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        if (_bitmap != null) {
            _bitmap.recycle();
        }
        _bitmap = null;
    }
}

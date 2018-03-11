/*#######################################################
 *
 *   Maintained by Gregor Santner, 2017-
 *   https://gsantner.net/
 *
 *   License: Apache 2.0
 *  https://github.com/gsantner/opoc/#licensing
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package net.gsantner.opoc.preference.nonsupport;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import net.gsantner.opoc.util.ContextUtils;

import io.github.froodyapp.R;

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
            _bitmap = new ContextUtils(getContext()).drawTextToDrawable(R.drawable.empty_64dp, String.valueOf(_currentValue), textSize);
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

package
        customfonts;

import
        android.content.Context;
import
        android.graphics.Typeface;
import
        android.util.AttributeSet;
import
        android.widget.EditText;

public
class
EditTextSFProDisplayRegular
        extends
        android.support.v7.widget.AppCompatEditText {


    public EditTextSFProDisplayRegular(Context
                                               context,
                                       AttributeSet
                                               attrs,
                                       int
                                               defStyle) {


        super(context,
                attrs,
                defStyle);


        init();


    }


    public EditTextSFProDisplayRegular(Context
                                               context,
                                       AttributeSet
                                               attrs) {


        super(context,
                attrs);


        init();


    }


    public EditTextSFProDisplayRegular(Context
                                               context) {


        super(context);


        init();


    }


    private void
    init() {


        if
        (!isInEditMode()) {


            setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/NeoSans_Pro_Regular.ttf"));


        }


    }
}

package com.pr.swalert;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import static com.pr.swalert.Rotate3dAnimation.RollType.BY_X;


public class Rotate3dAnimation extends Animation {
    private int mPivotXType = ABSOLUTE;
    private int mPivotYType = ABSOLUTE;
    private float mPivotXValue = 0.0f;
    private float mPivotYValue = 0.0f;

    private float mFromDegrees;
    private float mToDegrees;
    private float mPivotX;
    private float mPivotY;
    private Camera mCamera;
    private RollType mRollType;

    public enum RollType {
        BY_X(0), BY_Y(1), BY_Z(2);
        private int rollType;

        private static SparseArray<RollType> map = new SparseArray<>();

        static {
            for (RollType rollEnum : RollType.values()) {
                map.put(rollEnum.rollType, rollEnum);
            }
        }

        RollType(final int roll) {
            rollType = roll;
        }

        public static RollType valueOf(int rollType) {
            return map.get(rollType);
        }
    }

    protected static class Description {
        public int type;
        public float value;
    }

    private Description parseValue(TypedValue value) {
        Description d = new Description();
        if (value == null) {
            d.type = ABSOLUTE;
            d.value = 0;
        } else {
            if (value.type == TypedValue.TYPE_FRACTION) {
                d.type = (value.data & TypedValue.COMPLEX_UNIT_MASK) ==
                        TypedValue.COMPLEX_UNIT_FRACTION_PARENT ?
                        RELATIVE_TO_PARENT : RELATIVE_TO_SELF;
                d.value = TypedValue.complexToFloat(value.data);
                return d;
            } else if (value.type == TypedValue.TYPE_FLOAT) {
                d.type = ABSOLUTE;
                d.value = value.getFloat();
                return d;
            } else if (value.type >= TypedValue.TYPE_FIRST_INT &&
                    value.type <= TypedValue.TYPE_LAST_INT) {
                d.type = ABSOLUTE;
                d.value = value.data;
                return d;
            }
        }

        d.type = ABSOLUTE;
        d.value = 0.0f;

        return d;
    }

    public Rotate3dAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Rotate3dAnimation);

        mFromDegrees = a.getFloat(R.styleable.Rotate3dAnimation_fromDeg, 0.0f);
        mToDegrees = a.getFloat(R.styleable.Rotate3dAnimation_toDeg, 0.0f);
        mRollType = RollType.valueOf(a.getInt(R.styleable.Rotate3dAnimation_rollType, BY_X.rollType));
        Description d = parseValue(a.peekValue(R.styleable.Rotate3dAnimation_alPivotX));
        mPivotXType = d.type;
        mPivotXValue = d.value;

        d = parseValue(a.peekValue(R.styleable.Rotate3dAnimation_alPivotY));
        mPivotYType = d.type;
        mPivotYValue = d.value;

        a.recycle();

        initializePivotPoint();
    }

    public Rotate3dAnimation(RollType rollType, float fromDegrees, float toDegrees) {
        mRollType = rollType;
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mPivotX = 0.0f;
        mPivotY = 0.0f;
    }

    public Rotate3dAnimation(RollType rollType, float fromDegrees, float toDegrees, float pivotX, float pivotY) {
        mRollType = rollType;
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;

        mPivotXType = ABSOLUTE;
        mPivotYType = ABSOLUTE;
        mPivotXValue = pivotX;
        mPivotYValue = pivotY;
        initializePivotPoint();
    }

    public Rotate3dAnimation(RollType rollType, float fromDegrees, float toDegrees, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        mRollType = rollType;
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;

        mPivotXValue = pivotXValue;
        mPivotXType = pivotXType;
        mPivotYValue = pivotYValue;
        mPivotYType = pivotYType;
        initializePivotPoint();
    }

    private void initializePivotPoint() {
        if (mPivotXType == ABSOLUTE) {
            mPivotX = mPivotXValue;
        }
        if (mPivotYType == ABSOLUTE) {
            mPivotY = mPivotYValue;
        }
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
        mPivotX = resolveSize(mPivotXType, mPivotXValue, width, parentWidth);
        mPivotY = resolveSize(mPivotYType, mPivotYValue, height, parentHeight);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final Matrix matrix = t.getMatrix();

        mCamera.save();
        switch (mRollType) {
            case BY_X:
                mCamera.rotateX(degrees);
                break;
            case BY_Y:
                mCamera.rotateY(degrees);
                break;
            case BY_Z:
                mCamera.rotateZ(degrees);
                break;
        }
        mCamera.getMatrix(matrix);
        mCamera.restore();

        matrix.preTranslate(-mPivotX, -mPivotY);
        matrix.postTranslate(mPivotX, mPivotY);
    }
}

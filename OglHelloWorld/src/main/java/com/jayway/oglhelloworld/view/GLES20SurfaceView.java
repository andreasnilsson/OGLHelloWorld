package com.jayway.oglhelloworld.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jayway.oglhelloworld.ogl.GLObject;
import com.jayway.oglhelloworld.ogl.GLObjectDB;
import com.jayway.oglhelloworld.renderer.GLES20Renderer;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Andreas Nilsson
 */
public class GLES20SurfaceView extends GLSurfaceView {

    // Constants
    private static final long ANIMATION_DELAY = 16;
    private static final float ANIMATION_DELAY_IN_S = 0.016f;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    // Renderer
    private final GLES20Renderer mRenderer;

    // Touch
    private float mPreviousX;
    private float mPreviousY;

    // Animation
    private boolean mIsAnimating = false;
    private GLObject mAnimationTarget;

    private final Runnable mAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsAnimating) {
                if (mAnimationTarget != null) {
                    mAnimationTarget.update(ANIMATION_DELAY_IN_S);
                }

                requestRender();
                postDelayed(mAnimationRunnable, ANIMATION_DELAY);
            }
        }
    };

    private Observer mObjectDBObserver = new Observer() {
        @Override
        public void update(final Observable observable, final Object data) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mRenderer.setTarget((GLObject) data);
                    requestRender();
                }
            });
        }
    };

    public GLES20SurfaceView(final Context context) {
        super(context);

        mRenderer = new GLES20Renderer(context);
        setup();

    }

    public GLES20SurfaceView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        mRenderer = new GLES20Renderer(context);
        setup();
    }

    private void setup() {
        setEGLContextClientVersion(2);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        // Stop the GL context from being destroyed when the the activity pauses.
        setPreserveEGLContextOnPause(true);

        // Add observer so that we can know when the target has changed
        GLObjectDB.getInstance().addObserver(mObjectDBObserver);

        //TODO gesture detector

    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final GLObject glObject = GLObjectDB.getInstance().getSelectedObject();

        requestRender();
        return true;
    }

    public void toggleAnimation() {
        setAnimating(!isAnimating());
    }

    public boolean setAnimating(final boolean doAnimate) {
        mIsAnimating = doAnimate;
        final int animationDelayInMilliseconds = 16;

        if (mIsAnimating) {
            mAnimationTarget = GLObjectDB.getInstance().getSelectedObject();
            postDelayed(mAnimationRunnable, animationDelayInMilliseconds);
        } else {
            removeCallbacks(mAnimationRunnable);
        }

        return mIsAnimating;
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }

    /**
     * Changes to the next object and returns the title of it.
     *
     * @return The title of the next set object.
     */
    public String nextObject() {
        setAnimating(false);

        String title = GLObjectDB.getInstance().nextObject().title;

        requestRender();

        return title;
    }
}

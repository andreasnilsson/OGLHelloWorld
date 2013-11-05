package com.jayway.oglhelloworld.main;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jayway.oglhelloworld.gl.GlObject;
import com.jayway.oglhelloworld.gl.GlObjectManager;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Andreas Nilsson
 */
public class GLES20SurfaceView extends GLSurfaceView {
    // Constants
    private static final long ANIMATION_DELAY = 16; // In milliseconds
    private static final float ANIMATION_DELAY_IN_S = ANIMATION_DELAY / 1000f;

    // Renderer
    private final GLES20Renderer mRenderer;

    // Touch
    private float mPreviousX;
    private float mPreviousY;

    // Animation
    private boolean mIsAnimating = false;
    private GlObject mAnimationTarget;

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
                    mRenderer.setTarget((GlObject) data);
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

        // Let the gl wrapper check for errors
        setDebugFlags(DEBUG_CHECK_GL_ERROR);
        // Stop the GL context from being destroyed when the the activity pauses.
        setPreserveEGLContextOnPause(true);

        // Add observer so that we can know when the target has changed
        GlObjectManager.getInstance().addObserver(mObjectDBObserver);

        //TODO gesture detector

    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final float touchScaleFactor = 180.0f / 320;
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                final GlObject glObject = GlObjectManager.getInstance().getSelectedObject();


                float newRotationX = glObject.getRotationX() + dx * touchScaleFactor;
                float newRotationY = glObject.getRotationY() + dy * touchScaleFactor;

                glObject.setRotationX(newRotationX);
                glObject.setRotationY(newRotationY);

                requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;

        return true;
    }

    public void toggleAnimation() {
        setAnimating(!isAnimating());
    }

    public boolean setAnimating(final boolean doAnimate) {
        mIsAnimating = doAnimate;

        if (mIsAnimating) {
            mAnimationTarget = GlObjectManager.getInstance().getSelectedObject();
            postDelayed(mAnimationRunnable, ANIMATION_DELAY);
        } else {
            removeCallbacks(mAnimationRunnable);
        }

        return mIsAnimating;
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }

}

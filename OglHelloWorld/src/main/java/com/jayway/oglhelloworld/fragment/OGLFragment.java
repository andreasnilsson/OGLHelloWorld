package com.jayway.oglhelloworld.fragment;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jayway.oglhelloworld.R;
import com.jayway.oglhelloworld.ogl.GLES20Renderer;
import com.jayway.oglhelloworld.util.Log;

public class OGLFragment extends Fragment {
    private static final Log LOG = new Log(OGLFragment.class);

    private GLSurfaceView mGLSurfaceView;
    private GLES20Renderer mRenderer;
    private boolean mIsAnimating = false;

    public static OGLFragment newInstance() {
        return new OGLFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
        if (!detectOGL2Plus()) {
            LOG.d("Could not detect OGL 2+");
        } else {
            LOG.d("OGL 2+ is supported");
        }

        final View v = inflater.inflate(R.layout.fragment_ogl, root, false);
        if (v != null) {
            mGLSurfaceView = (GLSurfaceView) v.findViewById(R.id.surfaceView);
            setupGLSurfaceView(mGLSurfaceView);
        }

        setHasOptionsMenu(true);

        return v;
    }

    private boolean detectOGL2Plus() {
        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x20000);
    }

    private void setupGLSurfaceView(final GLSurfaceView glSurfaceView) {

        // Setup gl surface
        glSurfaceView.setEGLContextClientVersion(2);

        // Set our renderer
        mRenderer = new GLES20Renderer(getActivity());
        glSurfaceView.setRenderer(mRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        // Stop the GL context from being destroyed when the the activity pauses.
        glSurfaceView.getPreserveEGLContextOnPause();

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.ogl_fragment_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_animation:
                toggleAnimation();
                item.setTitle("Animation: " + (mIsAnimating ? "On" : "Off"));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleAnimation() {
        final int animationDelayInMilliseconds = 16;
        final float animationDelayInSeconds = 0.016f;
        mIsAnimating = !mIsAnimating;



        final GLES20Renderer.GLObject glObject = mRenderer.getGlObject();
        mGLSurfaceView.postDelayed(new Runnable() {
            float angle = 0;

            @Override
            public void run() {
                if (mIsAnimating) {
                    glObject.update(animationDelayInSeconds);
                    mGLSurfaceView.requestRender();
                    mGLSurfaceView.postDelayed(this, animationDelayInMilliseconds);
                }
            }
        }, animationDelayInMilliseconds);
    }
}

package com.jayway.oglhelloworld.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jayway.oglhelloworld.R;
import com.jayway.oglhelloworld.util.Log;

public class OGLFragment extends Fragment {
    private static final Log LOG = new Log(OGLFragment.class);

    private GLES20SurfaceView mGLSurfaceView;
    private MenuItem mToggleAnimationMenuItem;

    public static OGLFragment newInstance() {
        return new OGLFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
        if (!detectOGL2Plus()) {
            throw new RuntimeException("OpenGL ES 2+: NOT supported!");
        } else {
            LOG.i("OpenGL ES 2+: Supported");
        }

        final View v = inflater.inflate(R.layout.fragment_ogl, root, false);
        if (v != null) {
            mGLSurfaceView = (GLES20SurfaceView) v.findViewById(R.id.surfaceView);
        }

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.ogl_fragment, menu);

        mToggleAnimationMenuItem = menu.findItem(R.id.action_toggle_animation);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        boolean wasItemSelected = false;
        switch (item.getItemId()) {
            case R.id.action_toggle_animation:
                mGLSurfaceView.toggleAnimation();
                wasItemSelected = true;
                break;
        }

        final String animationTitle = mGLSurfaceView.isAnimating()
                ? getString(R.string.animation_on)
                : getString(R.string.animation_off);

        mToggleAnimationMenuItem.setTitle(animationTitle);
        return wasItemSelected || super.onOptionsItemSelected(item);
    }

    /**
     * Checks whether OpenGL 2.0+ is supported.
     *
     * @return {@code true} If support is found or the device is identified as an emulator.
     */
    private boolean detectOGL2Plus() {
        final Activity activity = getActivity();
        boolean isEmulator = Build.PRODUCT.startsWith("sdk");

        ConfigurationInfo info = null;
        if (activity != null && !isEmulator) {
            ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            info = am.getDeviceConfigurationInfo();
        }

        return (info != null && (info.reqGlEsVersion >= 0x20000)) || isEmulator;
    }
}

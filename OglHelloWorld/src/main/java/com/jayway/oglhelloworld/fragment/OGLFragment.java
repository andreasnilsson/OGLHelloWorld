package com.jayway.oglhelloworld.fragment;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jayway.oglhelloworld.R;
import com.jayway.oglhelloworld.util.Log;
import com.jayway.oglhelloworld.view.GLES20SurfaceView;

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
            final String message = "Could not detect OGL 2+";
            LOG.e(message);

            throw new RuntimeException(message);
        } else {
            LOG.i("OGL 2+ is supported");
        }

        final View v = inflater.inflate(R.layout.fragment_ogl, root, false);
        if (v != null) {
            mGLSurfaceView = (GLES20SurfaceView) v.findViewById(R.id.surfaceView);
        }

        setHasOptionsMenu(true);

        return v;
    }

    private boolean detectOGL2Plus() {
        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info != null && (info.reqGlEsVersion >= 0x20000);
    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.ogl_fragment_menu, menu);

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
        String animationTitle = "Animation: " + (mGLSurfaceView.isAnimating() ? "On" : "Off");
        mToggleAnimationMenuItem.setTitle(animationTitle);
        return wasItemSelected || super.onOptionsItemSelected(item);
    }
}

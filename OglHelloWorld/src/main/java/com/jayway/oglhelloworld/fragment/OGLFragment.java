package com.jayway.oglhelloworld.fragment;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jayway.oglhelloworld.R;

import com.jayway.oglhelloworld.ogl.GLES20Renderer;

public class OGLFragment extends Fragment{

    private static final String TAG = OGLFragment.class.getSimpleName();

    private static final int COLOR_BITS   = 8;
    private static final int ALPHA_BITS   = 8;
    private static final int DEPTH_BITS   = 16;
    private static final int STENCIL_BITS = 0;

    public static OGLFragment newInstance() {
        return new OGLFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
        if(!detectOGL2Plus()) {
            Log.d(TAG, "Could not detect OGL 2+");
        } {
            Log.d(TAG, "OGL 2+ is supported");
        }



        final View v = inflater.inflate(R.layout.fragment_ogl, root, false);
        if(v != null){
            setupGLSurfaceView( (GLSurfaceView) v.findViewById(R.id.surfaceView));
        }

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
//        glSurfaceView.setEGLConfigChooser(COLOR_BITS, COLOR_BITS, COLOR_BITS, ALPHA_BITS, DEPTH_BITS, STENCIL_BITS);

        // Set our renderer
        glSurfaceView.setRenderer(new GLES20Renderer(getActivity()));

        // Set rendering to 60 FPS
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        // Making sure that when the application pauses the context won't be recreated
        glSurfaceView.getPreserveEGLContextOnPause();

    }


}

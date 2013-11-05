package ui;

import android.app.Fragment;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jayway.oglhelloworld.R;

import ogl.GLES20Renderer;

public class OGLFragment extends Fragment{

    public static OGLFragment newInstance() {
        return new OGLFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
        System.out.println("on create view");


        final View v = inflater.inflate(R.layout.fragment_ogl, root, false);
        GLSurfaceView glSurfaceView = (GLSurfaceView) v.findViewById(R.id.surfaceView);
//
//
//        // Setup gl surface
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(true);
        glSurfaceView.setRenderer(new GLES20Renderer());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);


        return v;
    }
}

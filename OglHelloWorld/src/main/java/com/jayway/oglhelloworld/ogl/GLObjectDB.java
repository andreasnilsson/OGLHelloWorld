package com.jayway.oglhelloworld.ogl;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * A simple singleton of an in memory data base of {@link com.jayway.oglhelloworld.ogl.GLObject}.
 *
 * @author Andreas Nilsson
 */
public class GLObjectDB extends Observable {
    private static final boolean USE_UV = true;
    private static final boolean USE_NORMALs = true;

    private static GLObjectDB sInstance;
    private int mSelectedObject = 0;
    private List<GLObject> mAllObjects = new ArrayList<>();


    public GLObjectDB() {
        // create all objects..
        mAllObjects.add(GLObjectFactory.createSimpleTriangle(USE_UV, USE_NORMALs));
        mAllObjects.add(GLObjectFactory.createSimpleQuad(USE_UV, USE_NORMALs));
        mAllObjects.add(GLObjectFactory.createCube(1, 1, 1, USE_UV, USE_NORMALs));
        mAllObjects.add(GLObjectFactory.createCubeWithFlatNormals(1, 1, 1, USE_UV, USE_NORMALs));
        mAllObjects.add(GLObjectFactory.createTorus(0.7f, 0.4f, 40, 40, USE_UV, USE_NORMALs));
        // TODO add more objects here
    }

    public static GLObjectDB getInstance() {
        if (sInstance == null) {
            sInstance = new GLObjectDB();
        }

        return sInstance;
    }

    public GLObject getSelectedObject() {
        return mAllObjects.get(mSelectedObject);
    }

    public GLObject nextObject() {
        mSelectedObject = mSelectedObject == mAllObjects.size() - 1 ? 0 : mSelectedObject + 1;

        notifyObservers(getSelectedObject());

        return getSelectedObject();
    }

    public String[] getObjectTitles() {
        String[] titles = new String[mAllObjects.size()];

        for (int i = 0; i < mAllObjects.size(); i++) {
            titles[i] = mAllObjects.get(i).title;
        }

        return titles;
    }

    public int getSelectedIndex() {
        return mSelectedObject;
    }

    public void setSelectedObject(final int position) {
        mSelectedObject = position;
        setChanged();
        notifyObservers(getSelectedObject());
    }
}

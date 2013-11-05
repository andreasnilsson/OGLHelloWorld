package com.jayway.oglhelloworld.gl;

import java.util.ArrayList;
import java.util.Observable;

/**
 * A simple singleton of an in memory manager of {@link GlObject}.
 *
 * @author Andreas Nilsson
 */
public class GlObjectManager extends Observable {
    private static final boolean USE_UV = true;
    private static final boolean USE_NORMALs = true;

    private static GlObjectManager sInstance;
    private int mSelectedObject = 0;
    private ArrayList<GlObject> mAllObjects = new ArrayList<>();


    public GlObjectManager() {
        mAllObjects.add(GlObjectFactory.createSimpleTriangle(USE_UV, USE_NORMALs));
        mAllObjects.add(GlObjectFactory.createSimpleQuad(USE_UV, USE_NORMALs));
        mAllObjects.add(GlObjectFactory.createCube(1, 1, 1, USE_UV, USE_NORMALs));
        mAllObjects.add(GlObjectFactory.createCubeWithFlatNormals(1, 1, 1, USE_UV, USE_NORMALs));
        mAllObjects.add(GlObjectFactory.createTorus(0.7f, 0.4f, 40, 40, USE_UV, USE_NORMALs));
        // TODO add more objects here
    }

    public static GlObjectManager getInstance() {
        if (sInstance == null) {
            sInstance = new GlObjectManager();
        }

        return sInstance;
    }

    public GlObject getSelectedObject() {
        return mAllObjects.get(mSelectedObject);
    }

    public String[] getObjectTitles() {
        String[] titles = new String[mAllObjects.size()];

        for (int i = 0; i < mAllObjects.size(); i++) {
            titles[i] = mAllObjects.get(i).title;
        }

        return titles;
    }

    public void setSelectedObject(final int position) {
        mSelectedObject = position;
        setChanged();
        notifyObservers(getSelectedObject());
    }
}

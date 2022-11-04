package server.impl;

import server.interfaces.ICoordinate;

public class Coordinate implements ICoordinate {
    private final int mX;
    private final int mY;

    public Coordinate(int x, int y) {
        mX = x;
        mY = y;
    }

    @Override
    public int getX() {
        return mX;
    }

    @Override
    public int getY() {
        return mY;
    }
}

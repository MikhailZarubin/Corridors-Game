package util.impl;

import server.impl.Coordinate;
import server.interfaces.ICoordinate;
import util.interfaces.ICorridorDescription;

public class CorridorDescription implements ICorridorDescription {
    private final Coordinate mCoordinate;
    private final CorridorPosition mCorridorPosition;

    public CorridorDescription(Coordinate coordinate, CorridorPosition corridorPosition) {
        mCoordinate = coordinate;
        mCorridorPosition = corridorPosition;
    }

    @Override
    public ICoordinate getCoordinate() {
        return mCoordinate;
    }

    @Override
    public CorridorPosition getCorridorPosition() {
        return mCorridorPosition;
    }
}

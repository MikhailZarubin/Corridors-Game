package util.impl;

import server.impl.Coordinate;
import server.interfaces.ICoordinate;
import util.interfaces.ICorridorDescription;
import util.interfaces.ITurnResult;

public class TurnResult implements ITurnResult {
    private final boolean mIsSuccessful;

    private final CorridorDescription mFilledCorridorRoot;
    private final CorridorDescription mFilledCorridorNeighbor;

    private final Coordinate mFilledCellRoot;
    private final Coordinate mFilledCellNeighbor;
    private final boolean mIsFullFilled;

    public TurnResult(boolean isSuccessful,
                      ICorridorDescription fillerCorridorRoot, ICorridorDescription filledCorridorNeighbor,
                      ICoordinate filledCellRoot, ICoordinate filledCellNeighbor,
                      boolean isFullFilled) {
        mIsSuccessful = isSuccessful;

        mFilledCorridorRoot = (CorridorDescription) fillerCorridorRoot;
        mFilledCorridorNeighbor = (CorridorDescription) filledCorridorNeighbor;

        mFilledCellRoot = (Coordinate) filledCellRoot;
        mFilledCellNeighbor = (Coordinate) filledCellNeighbor;

        mIsFullFilled = isFullFilled;
    }

    @Override
    public boolean isSuccessful() {
        return mIsSuccessful;
    }

    @Override
    public ICoordinate getFilledRootCell() {
        return mFilledCellRoot;
    }

    @Override
    public ICoordinate getFilledNeighborCell() {
        return mFilledCellNeighbor;
    }

    @Override
    public ICorridorDescription getFilledRootCorridor() {
        return mFilledCorridorRoot;
    }

    @Override
    public ICorridorDescription getFilledNeighborCorridor() {
        return mFilledCorridorNeighbor;
    }

    @Override
    public boolean isFullFilled() {
        return mIsFullFilled;
    }
}

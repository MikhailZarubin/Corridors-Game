package server.impl;

import server.interfaces.ICorridorsCell;
import util.CellState;
import util.Constants;
import util.impl.CorridorPosition;

import java.util.ArrayList;

public class CorridorsCell implements ICorridorsCell {
    private final ArrayList<CorridorPosition> mCorridorPositions = new ArrayList<>();

    public CorridorsCell() {}

    @Override
    public void addCorridor(CorridorPosition corridorPosition) {
        if (!mCorridorPositions.contains(corridorPosition)) {
            mCorridorPositions.add(corridorPosition);
        }
    }

    @Override
    public CellState getCellState() {
        return mCorridorPositions.size() == Constants.MAX_CORRIDORS_IN_CELL ? CellState.FILLED : CellState.FREE;
    }
}

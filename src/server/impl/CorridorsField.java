package server.impl;

import server.interfaces.ICoordinate;
import server.interfaces.ICorridorsCell;
import server.interfaces.ICorridorsField;
import util.*;
import util.impl.CorridorDescription;
import util.impl.CorridorPosition;
import util.impl.TurnResult;
import util.interfaces.ICorridorDescription;
import util.interfaces.ITurnResult;

public class CorridorsField implements ICorridorsField {
    private final ICorridorsCell[][] mCorridorsField = new CorridorsCell[Constants.FIELD_SIZE][Constants.FIELD_SIZE];
    private int mFilledCell = 0;

    public CorridorsField() {
        for (int i = 0; i < Constants.FIELD_SIZE; i++) {
            for (int j = 0; j < Constants.FIELD_SIZE; j++) {
                mCorridorsField[i][j] = new CorridorsCell();
            }
        }
    }

    @Override
    public TurnResult turn(ICorridorDescription corridorDescription) {
        ITurnResult baseResult = analysisCorridorDescription(corridorDescription, false);
        ITurnResult resultAnalysisNeighbor = analysisCorridorDescription(analysisNeighbors(corridorDescription), true);

        boolean isSuccessful = baseResult.isSuccessful() || resultAnalysisNeighbor.isSuccessful();

        ICorridorDescription filledCorridorRoot = baseResult.getFilledRootCorridor();
        ICorridorDescription filledCorridorNeighbor = resultAnalysisNeighbor.getFilledNeighborCorridor();

        ICoordinate filledCellRoot = baseResult.getFilledRootCell();
        ICoordinate filledCellNeighbor = resultAnalysisNeighbor.getFilledNeighborCell();

        return new TurnResult(isSuccessful, filledCorridorRoot, filledCorridorNeighbor, filledCellRoot, filledCellNeighbor, isFilled());
    }

    @Override
    public boolean isFilled() {
        return mFilledCell == getFieldSize();
    }

    private boolean checkValidCoordinates(int x, int y) {
        return x >= 0 && y >= 0 && x < Constants.FIELD_SIZE && y < Constants.FIELD_SIZE;
    }

    private ICorridorDescription analysisNeighbors(ICorridorDescription corridorDescription) {
        int x = corridorDescription.getCoordinate().getX();
        int y = corridorDescription.getCoordinate().getY();
        CorridorPosition corridorPosition = corridorDescription.getCorridorPosition();

        int neighborX = 0;
        int neighborY = 0;
        CorridorPosition neighborCorridorPosition = null;

        switch (corridorPosition) {
            case TOP -> {
                neighborX = x - 1;
                neighborY = y;
                neighborCorridorPosition = CorridorPosition.BOTTOM;
            }
            case BOTTOM -> {
                neighborX = x + 1;
                neighborY = y;
                neighborCorridorPosition = CorridorPosition.TOP;
            }
            case LEFT -> {
                neighborX = x;
                neighborY = y - 1;
                neighborCorridorPosition = CorridorPosition.RIGHT;
            }
            case RIGHT -> {
                neighborX = x;
                neighborY = y + 1;
                neighborCorridorPosition = CorridorPosition.LEFT;
            }
        }

        return new CorridorDescription(new Coordinate(neighborX, neighborY), neighborCorridorPosition);
    }

    private ITurnResult analysisCorridorDescription(ICorridorDescription corridorDescription, boolean isNeighbor) {
        int x = corridorDescription.getCoordinate().getX();
        int y = corridorDescription.getCoordinate().getY();
        CorridorPosition corridorPosition = corridorDescription.getCorridorPosition();

        boolean isSuccessfulTurn = false;
        ICorridorDescription filledCorridor = null;
        ICoordinate filledCell = null;

        if (checkValidCoordinates(x, y)) {
            filledCorridor = corridorDescription;
            CellState lastCellState = mCorridorsField[x][y].getCellState();
            mCorridorsField[x][y].addCorridor(corridorPosition);
            CellState newCellState = mCorridorsField[x][y].getCellState();
            if (!newCellState.equals(CellState.INCORRECT) && !newCellState.equals(lastCellState)) {
                mFilledCell++;
                isSuccessfulTurn = true;
                filledCell = corridorDescription.getCoordinate();
            }
        }

        TurnResult turnResult;
        if (isNeighbor) {
            turnResult = new TurnResult(isSuccessfulTurn, null, filledCorridor, null, filledCell, isFilled());
        } else {
            turnResult = new TurnResult(isSuccessfulTurn, filledCorridor, null, filledCell, null, isFilled());
        }
        return turnResult;
    }

    @Override
    public Integer getFieldSize() {
        return Constants.FIELD_SIZE * Constants.FIELD_SIZE;
    }
}

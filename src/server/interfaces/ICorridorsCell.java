package server.interfaces;

import util.CellState;
import util.impl.CorridorPosition;

public interface ICorridorsCell {
    void addCorridor(CorridorPosition corridorPosition);
    CellState getCellState();
}

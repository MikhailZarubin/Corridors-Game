package util.interfaces;

import server.interfaces.ICoordinate;

import java.io.Serializable;
public interface ITurnResult extends Serializable {
    boolean isSuccessful();
    ICorridorDescription getFilledRootCorridor();
    ICorridorDescription getFilledNeighborCorridor();
    ICoordinate getFilledRootCell();
    ICoordinate getFilledNeighborCell();
    boolean isFullFilled();
}

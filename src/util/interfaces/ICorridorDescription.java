package util.interfaces;

import server.interfaces.ICoordinate;
import util.impl.CorridorPosition;

import java.io.Serializable;

public interface ICorridorDescription extends Serializable {
    ICoordinate getCoordinate();
    CorridorPosition getCorridorPosition();
}

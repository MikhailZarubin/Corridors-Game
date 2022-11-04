package server.interfaces;

import util.impl.TurnResult;
import util.interfaces.ICorridorDescription;

public interface ICorridorsField {
    TurnResult turn(ICorridorDescription corridorDescription);
    boolean isFilled();
    Integer getFieldSize();
}

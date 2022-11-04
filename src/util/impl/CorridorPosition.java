package util.impl;

import util.Constants;

import java.io.Serializable;

public enum CorridorPosition implements Serializable {
    LEFT(Constants.LEFT_POSITION_STR),
    RIGHT(Constants.RIGHT_POSITION_STR),
    TOP(Constants.TOP_POSITION_STR),
    BOTTOM(Constants.BOTTOM_POSITION_STR);

    private final String mPositionStr;
    CorridorPosition(String positionStr) {
        mPositionStr = positionStr;
    }

    public String getPositionStr() {
        return mPositionStr;
    }
}

package client.interfaces;

import util.impl.CorridorPosition;

import java.awt.*;

public interface IUICell {
    void applyColor(Color newColor);
    void hideCorridor(CorridorPosition corridorPosition);
}

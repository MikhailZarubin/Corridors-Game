package client.interfaces;

import client.impl.UIState;
import server.interfaces.ICoordinate;
import util.interfaces.ICorridorDescription;

import java.awt.*;
import java.util.ArrayList;

public interface IUICorridors {
    void setTitle(String title);
    void setState(UIState state);
    void applyCellsColor(ArrayList<ICoordinate> cells, Color newColor);
    void hideCorridors(ArrayList<ICorridorDescription> filledCorridors);
}

package client.interfaces;

import server.impl.Coordinate;
import util.impl.CorridorPosition;

import java.io.IOException;

public interface IParentView {
    void handleClick(Coordinate coordinate, CorridorPosition corridorPosition) throws IOException;
}

package client.interfaces;

import util.impl.CorridorDescription;

import java.io.IOException;

public interface ICorridorsClient {
    void handleClick(CorridorDescription corridorDescription) throws IOException;
    void handleCloseFrame();
}

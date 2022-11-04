package client.impl;

import client.interfaces.ICorridorsClient;
import client.interfaces.IParentView;
import client.interfaces.IUICell;
import client.interfaces.IUICorridors;
import server.impl.Coordinate;
import server.interfaces.ICoordinate;
import util.Constants;
import util.impl.CorridorDescription;
import util.impl.CorridorPosition;
import util.interfaces.ICorridorDescription;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

public class UICorridors implements IUICorridors, IParentView {
    private final JFrame mFrame = new JFrame();
    private final IUICell[][] mCells = new UICell[Constants.FIELD_SIZE][Constants.FIELD_SIZE];
    private final ICorridorsClient mCorridorsClient;

    public UICorridors() throws IOException, ClassNotFoundException {
        mCorridorsClient = new CorridorsClient(this);
        initUI();
    }

    private void initUI() {
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mFrame.setSize(Constants.UI_FRAME_WIDTH, Constants.UI_FRAME_HEIGHT);
        mFrame.setResizable(false);
        mFrame.setLayout(new GridLayout(Constants.FIELD_SIZE, Constants.FIELD_SIZE, Constants.UI_FRAME_GAP, Constants.UI_FRAME_GAP));

        for (int x = 0; x < Constants.FIELD_SIZE; x++) {
            for (int y = 0; y < Constants.FIELD_SIZE; y++) {
                UICell cell = new UICell(this, new Coordinate(x, y));
                mCells[x][y] = cell;
                mFrame.add(cell);
            }
        }

        mFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mCorridorsClient.handleCloseFrame();
                super.windowClosing(e);
            }
        });
        mFrame.setBackground(Constants.UI_BACKGROUND_COLOR);
        mFrame.setVisible(true);
    }

    @Override
    public void setTitle(String title) {
        mFrame.setTitle(title);
    }

    @Override
    public void setState(UIState state) {
        switch (state) {
            case ACTIVE -> {
                mFrame.getRootPane().setBorder(BorderFactory.createMatteBorder(Constants.UI_BORDER_SIZE, Constants.UI_BORDER_SIZE,
                        Constants.UI_BORDER_SIZE, Constants.UI_BORDER_SIZE, Constants.UI_BORDER_COLOR));
                mFrame.setEnabled(true);
            }
            case INACTIVE -> {
                mFrame.getRootPane().setBorder(BorderFactory.createEmptyBorder());
                mFrame.setEnabled(false);
            }
        }
    }

    @Override
    public void applyCellsColor(ArrayList<ICoordinate> cells, Color newColor) {
        cells.forEach(cell -> {
            mCells[cell.getX()][cell.getY()].applyColor(newColor);
        });
    }

    @Override
    public void handleClick(Coordinate coordinate, CorridorPosition corridorPosition) throws IOException {
        mCorridorsClient.handleClick(new CorridorDescription(coordinate, corridorPosition));
    }

    @Override
    public void hideCorridors(ArrayList<ICorridorDescription> filledCorridors) {
        for (ICorridorDescription filledCorridor : filledCorridors) {
            mCells[filledCorridor.getCoordinate().getX()][filledCorridor.getCoordinate().getY()].hideCorridor(filledCorridor.getCorridorPosition());
        }
    }
}

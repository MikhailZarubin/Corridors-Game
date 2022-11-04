package client.impl;

import client.interfaces.IParentView;
import client.interfaces.IUICell;
import server.impl.Coordinate;
import util.Constants;
import util.impl.CorridorPosition;

import javax.swing.*;
import java.awt.*;

public class UICell extends JPanel implements IUICell {
    private final IParentView mParentView;
    JButton[] mCorridorsButtons = new JButton[CorridorPosition.values().length];

    public UICell(IParentView parentView, Coordinate coordinate) {
        mParentView = parentView;

        setLayout(new GridLayout(CorridorPosition.values().length, 1));
        setBackground(Constants.BASE_CELL_COLOR);

        for (int i = 0; i < mCorridorsButtons.length; i++) {
            int finalI = i;
            mCorridorsButtons[i] = new JButton(CorridorPosition.values()[i].getPositionStr());
            mCorridorsButtons[i].addActionListener(actionEvent -> {
                try {
                    mParentView.handleClick(coordinate, CorridorPosition.values()[finalI]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            add(mCorridorsButtons[i]);
        }
    }

    @Override
    public void applyColor(Color newColor) {
        setBackground(newColor);
    }

    @Override
    public void hideCorridor(CorridorPosition corridorPosition) {
        for (JButton corridorsButton : mCorridorsButtons) {
            String corridorPositionStr = corridorsButton.getText();
            if (corridorPositionStr.equals(corridorPosition.getPositionStr())) {
                corridorsButton.setVisible(false);
            }
        }
    }
}

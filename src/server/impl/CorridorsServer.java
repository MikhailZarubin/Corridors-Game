package server.impl;

import server.interfaces.ICorridorsField;
import util.*;
import util.impl.JsonUtil;
import util.impl.ObjectToStringParser;
import util.impl.TurnResult;
import util.interfaces.ICorridorDescription;
import util.interfaces.ITurnResult;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class CorridorsServer {
    public static final int port = 9877;
    ServerSocket mCorridorsServer = new ServerSocket(port);
    private int mClientCount = 0;
    private final ArrayList<Socket> mOpenSockets = new ArrayList<>();
    private final ArrayList<String> mClientIds = new ArrayList<>();
    private String mActiveClientId;
    private String mInactiveClientId;
    private final HashMap<String, Integer> mClientToFilledCells = new HashMap<>();
    private final HashMap<String, ObjectInputStream> mClientToInputStreamMapper = new HashMap<>();
    private final HashMap<String, ObjectOutputStream> mClientToOutputStreamMapper = new HashMap<>();
    private final ICorridorsField mCorridorsField = new CorridorsField();

    public CorridorsServer() throws IOException, ClassNotFoundException {
        initGame();
        game();
    }

    private void initGame() throws IOException, ClassNotFoundException {
        while (mClientCount < Constants.MAX_CLIENT_COUNT) {
            Socket socket = mCorridorsServer.accept();

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            String inputClientMessage = ObjectToStringParser.parse(inputStream.readObject());

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            if (inputClientMessage != null &&
                    inputClientMessage.equals(Constants.DEFAULT_CLIENT_ID)) {
                String clientId = Constants.DEFAULT_CLIENT_ID + ' ' + mClientCount;
                outputStream.writeObject(clientId);
                mClientIds.add(clientId);
                mOpenSockets.add(socket);
                mClientToFilledCells.put(clientId, 0);
                mClientToInputStreamMapper.put(clientId, inputStream);
                mClientToOutputStreamMapper.put(clientId, outputStream);
                mClientCount++;
            } else {
                outputStream.writeObject(Constants.ERROR_ID);
                socket.close();
                inputStream.close();
                outputStream.close();
            }
        }
    }

    private void game() throws IOException {
        mClientToOutputStreamMapper.forEach((id, stream) -> {
            try {
                if (id.equals(mClientIds.get(Constants.STARTS_CLIENT_INDEX))) {
                    mActiveClientId = id;
                    stream.writeObject(Constants.MESSAGE_START_GAME + ' ' + Constants.ACTIVE_STATE);
                    stream.writeObject(JsonUtil.getGsonFromTurnResult(new TurnResult(true, null, null, null, null, false)));
                } else {
                    mInactiveClientId = id;
                    stream.writeObject(Constants.MESSAGE_START_GAME + ' ' + Constants.INACTIVE_STATE);
                    stream.writeObject(JsonUtil.getGsonFromTurnResult(new TurnResult(false, null, null, null, null, false)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ICorridorDescription corridorDescription;
        String jsonActiveClient;
        String jsonInactiveClient;
        TurnResult turnResultActiveClient;
        TurnResult turnResultInactiveClient;
        while (!mCorridorsField.isFilled()) {
            try {
                jsonActiveClient = ObjectToStringParser.parse(mClientToInputStreamMapper.get(mActiveClientId).readObject());
                corridorDescription = JsonUtil.getCorridorDescriptionFromGson(jsonActiveClient);
                turnResultActiveClient = mCorridorsField.turn(corridorDescription);
                turnResultInactiveClient = new TurnResult(!turnResultActiveClient.isSuccessful(), turnResultActiveClient.getFilledRootCorridor(), turnResultActiveClient.getFilledNeighborCorridor(), turnResultActiveClient.getFilledRootCell(), turnResultActiveClient.getFilledNeighborCell(), turnResultActiveClient.isFullFilled());

                jsonActiveClient = JsonUtil.getGsonFromTurnResult(turnResultActiveClient);
                jsonInactiveClient = JsonUtil.getGsonFromTurnResult(turnResultInactiveClient);

                String finalJsonActiveClient = jsonActiveClient;
                String finalJsonInactiveClient = jsonInactiveClient;
                mClientToOutputStreamMapper.forEach((id, stream) -> {
                    try {
                        if (id.equals(mActiveClientId)) {
                            stream.writeObject(finalJsonActiveClient);
                        } else {
                            stream.writeObject(finalJsonInactiveClient);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                updateActiveClientFilledCells(turnResultActiveClient);

                if (!turnResultActiveClient.isSuccessful()) {
                    swapClients();
                }

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        mClientToOutputStreamMapper.forEach((id, stream) -> {
            try {
                if (mClientToFilledCells.get(id) > mCorridorsField.getFieldSize() / 2) {
                    stream.writeObject(Constants.WINNER_MSG);
                } else {
                    stream.writeObject(Constants.LOSER_MSG);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        closeResources();
        mCorridorsServer.close();
    }

    private void updateActiveClientFilledCells(ITurnResult turnResult) {
        Integer filledCellsOnActiveClient = mClientToFilledCells.get(mActiveClientId);
        if (turnResult.getFilledRootCell() != null) {
            filledCellsOnActiveClient++;
        }
        if (turnResult.getFilledNeighborCell() != null) {
            filledCellsOnActiveClient++;
        }
        mClientToFilledCells.put(mActiveClientId, filledCellsOnActiveClient);
    }
    private void swapClients() {
        String tmp = mActiveClientId;
        mActiveClientId = mInactiveClientId;
        mInactiveClientId = tmp;
    }

    private void closeResources() {
        mClientToInputStreamMapper.forEach((id, stream) -> {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        mClientToInputStreamMapper.clear();
        mClientToOutputStreamMapper.forEach((id, stream) -> {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        mClientToOutputStreamMapper.clear();
        mOpenSockets.forEach((socket -> {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        mOpenSockets.clear();
    }
}


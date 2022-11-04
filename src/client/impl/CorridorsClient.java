package client.impl;

import client.interfaces.ICorridorsClient;
import client.interfaces.IUICorridors;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import server.impl.CorridorsServer;
import server.interfaces.ICoordinate;
import util.Constants;
import util.impl.CorridorDescription;
import util.impl.JsonUtil;
import util.impl.ObjectToStringParser;
import util.impl.TurnResult;
import util.interfaces.ICorridorDescription;
import util.interfaces.ITurnResult;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class CorridorsClient implements ICorridorsClient {
    private final IUICorridors mClientUI;
    private String mClientId = Constants.DEFAULT_CLIENT_ID;
    private ObjectInputStream mInputStream;
    private ObjectOutputStream mOutputStream;
    private Disposable mServerMessageSubscribe = null;
    private Disposable mServerAnswerSubscribe = null;
    private boolean isFinished = false;

    public CorridorsClient(IUICorridors clientUI) throws IOException, ClassNotFoundException {
        mClientUI = clientUI;
        mClientUI.setState(UIState.INACTIVE);
        initClient();
    }

    private void initClient() throws IOException, ClassNotFoundException {
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = new Socket(host.getHostName(), CorridorsServer.port);

        mOutputStream = new ObjectOutputStream(socket.getOutputStream());
        mOutputStream.writeObject(mClientId);

        mInputStream = new ObjectInputStream(socket.getInputStream());
        mClientId = ObjectToStringParser.parse(mInputStream.readObject());

        if (mClientId == null || mClientId.equals(Constants.ERROR_ID)) {
            mInputStream.close();
            mOutputStream.close();
        } else {
            configureServerMessageFlowable();
        }
    }

    private void configureServerAnswerFlowable() {
        Flowable<ITurnResult> serverAnswerFlowable = Flowable.create(this::waitServerAnswers, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single());
        mServerAnswerSubscribe = serverAnswerFlowable.subscribe(this::handleServerAnswer);
    }

    private void configureServerMessageFlowable() {
        Flowable<String> serverMessageFlowable = Flowable.create(this::waitServerMessage, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io());
        mServerMessageSubscribe = serverMessageFlowable.subscribe(this::handleServerMessage);
    }

    private void waitServerMessage(FlowableEmitter<String> emitter) {
        try {
            emitter.onNext(ObjectToStringParser.parse(mInputStream.readObject()));
            emitter.onComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitServerAnswers(FlowableEmitter<ITurnResult> emitter) {
        try {
            String json;
            TurnResult turnResult;
            boolean isFullFilled = false;
            while (!isFullFilled) {
                json = ObjectToStringParser.parse(mInputStream.readObject());
                turnResult = JsonUtil.getTurnResultFromJson(json);

                emitter.onNext(turnResult);
                isFullFilled = turnResult.isFullFilled();
            }
            emitter.onComplete();
        } catch (Exception e) {
            emitter.onError(e);
        }
    }

    private void handleServerAnswer(ITurnResult turnResult) {
        ArrayList<ICoordinate> filledCells = new ArrayList<>();
        if (turnResult.getFilledRootCell() != null) {
            filledCells.add(turnResult.getFilledRootCell());
        }
        if (turnResult.getFilledNeighborCell() != null) {
            filledCells.add(turnResult.getFilledNeighborCell());
        }

        if (turnResult.isSuccessful()) {
            mClientUI.setState(UIState.ACTIVE);
            mClientUI.applyCellsColor(filledCells, Constants.YOUR_COLOR);
        } else {
            mClientUI.setState(UIState.INACTIVE);
            mClientUI.applyCellsColor(filledCells, Constants.OPPONENT_COLOR);
        }

        ArrayList<ICorridorDescription> filledCorridors = new ArrayList<>();
        if (turnResult.getFilledRootCorridor() != null) {
            filledCorridors.add(turnResult.getFilledRootCorridor());
        }
        if (turnResult.getFilledNeighborCorridor() != null) {
            filledCorridors.add(turnResult.getFilledNeighborCorridor());
        }
        mClientUI.hideCorridors(filledCorridors);

        if (turnResult.isFullFilled()) {
            isFinished = true;
            mClientUI.setState(UIState.ACTIVE);
            configureServerMessageFlowable();
        }
    }

    private void handleServerMessage(String message) {
        System.out.println(message);
        if (!isFinished) {
            mClientUI.setTitle(mClientId);
            configureServerAnswerFlowable();
        } else {
            mClientUI.setTitle(message);
        }
    }

    @Override
    public void handleClick(CorridorDescription corridorDescription) throws IOException {
        mOutputStream.writeObject(JsonUtil.getGsonFromCorridorDescription(corridorDescription));
    }

    @Override
    public void handleCloseFrame() {
        try {
            if (mServerMessageSubscribe != null && !mServerMessageSubscribe.isDisposed()) {
                mServerMessageSubscribe.dispose();
            }
            if (mServerAnswerSubscribe != null && !mServerAnswerSubscribe.isDisposed()) {
                mServerAnswerSubscribe.dispose();
            }
            mOutputStream.close();
            mInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

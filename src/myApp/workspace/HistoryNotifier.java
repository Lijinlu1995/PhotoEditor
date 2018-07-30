package myApp.workspace;

import java.util.Observable;

public class HistoryNotifier extends Observable {

    public void notifyHistory() {
        setChanged();
        notifyObservers();
    }
}

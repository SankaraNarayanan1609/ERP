package com.Vcidex.StoryboardSystems.Common;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private List<Observer> observers = new ArrayList<>();
    private String poRefNo;
    private String status;
    private String pageName;

    // Register an observer
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    // Remove an observer
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    // Notify all observers
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(poRefNo, status, pageName);
        }
    }

    // Update state and notify observers
    public void setState(String poRefNo, String status, String pageName) {
        this.poRefNo = poRefNo;
        this.status = status;
        this.pageName = pageName;
        notifyObservers();
    }
}


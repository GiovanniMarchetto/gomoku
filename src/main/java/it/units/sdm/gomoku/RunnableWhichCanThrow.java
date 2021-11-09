package it.units.sdm.gomoku;

public interface RunnableWhichCanThrow<E extends Throwable> {
    void run() throws E;
}

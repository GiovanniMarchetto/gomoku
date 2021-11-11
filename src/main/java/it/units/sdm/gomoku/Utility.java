package it.units.sdm.gomoku;

import org.jetbrains.annotations.NotNull;

public class Utility {

    //    private static int threadCount = 0;
    public static void runOnSeparateThread(@NotNull final Runnable runnable) {
        var t = new Thread(runnable);
//        var name = t.getName();
//        new Thread(() -> {
//            System.out.println(name + " starting... (" + threadCount + " threads were already running)");
        t.start();
//            threadCount++;
//            try {
//                t.join();
//                threadCount--;
//                System.out.println(name + " ended (" + threadCount + " threads are still running)");
//            } catch (InterruptedException e) {
//                System.out.println(name + " interrupted wtf (" + threadCount + " threads are running)");
//            }
//
//        }).start();
    }
}

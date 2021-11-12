package it.units.sdm.gomoku;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public class Utility {

    public static Logger getLoggerOfClass(@NotNull final Class<?> targetClass) {
        return Logger.getLogger(Objects.requireNonNull(targetClass).getCanonicalName());
    }

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

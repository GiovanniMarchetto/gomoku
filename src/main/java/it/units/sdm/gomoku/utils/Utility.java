package it.units.sdm.gomoku.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public class Utility {

    public static boolean isEvenNumber(int n) {
        return n % 2 == 0;
    }

    public static Logger getLoggerOfClass(@NotNull final Class<?> targetClass) {    // TODO: test?
        return Logger.getLogger(Objects.requireNonNull(targetClass).getCanonicalName());
    }

    //    private static int threadCount = 0;
    public static Thread runOnSeparateThread(@NotNull final Runnable runnable) {    //TODO: test?
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
        return t;
    }
}

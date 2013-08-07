package com.squareup.otto.outside;

import com.squareup.otto.Bus;
import com.squareup.otto.Shuttle;
import com.squareup.otto.ShuttleDispatcher;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Test;

public class ThreadEnforcementTest {

  Bus bus;
  ExecutorService backgroundThread;

  @Before public void setUp() {
    bus = new Shuttle(ShuttleDispatcher.TEST);
    bus.enable();
    backgroundThread = Executors.newSingleThreadExecutor();
  }

  @Test(expected = AssertionError.class) public void registerEnforcesThread() throws Throwable {
    enforcesThread(new Runnable() {
      @Override public void run() {
        bus.register(new Object());
      }
    });
  }

  @Test(expected = AssertionError.class) public void postEnforcesThread() throws Throwable {
    enforcesThread(new Runnable() {
      @Override public void run() {
        bus.post(new Object());
      }
    });
  }

  @Test(expected = AssertionError.class) public void alsoEnforcesWhenDisabled() throws Throwable {
    bus.disable();
    enforcesThread(new Runnable() {
      @Override public void run() {
        bus.post(new Object());
      }
    });
  }

  @Test(expected = AssertionError.class) public void enableEnforcesThread() throws Throwable {
    bus.disable();
    enforcesThread(new Runnable() {
      @Override public void run() {
        bus.enable();
      }
    });
  }

  @Test(expected = AssertionError.class) public void disableEnforcesThread() throws Throwable {
    enforcesThread(new Runnable() {
      @Override public void run() {
        bus.disable();
      }
    });
  }

  @Test(expected = AssertionError.class) public void destroyEnforcesThread() throws Throwable {
    enforcesThread(new Runnable() {
      @Override public void run() {
        bus.destroy();
      }
    });
  }

  @Test(expected = AssertionError.class) public void spawnEnforcesThread() throws Throwable {
    enforcesThread(new Runnable() {
      @Override public void run() {
        bus.spawn();
      }
    });
  }

  public void enforcesThread(Runnable runnable) throws Throwable {
    Future<?> task = backgroundThread.submit(runnable);
    try {
      task.get();
    } catch (Exception e) {
      throw e.getCause();
    }
  }

}
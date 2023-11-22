package io.talk.vertx;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

import static java.lang.System.out;
import static java.util.stream.IntStream.range;

record EventLoop(Deque<Event> events, Map<String, Consumer<String>> listeners) {
  EventLoop() {this(new ConcurrentLinkedDeque<>(), new ConcurrentHashMap<>());}

  record Event(String name, String data) {}

  EventLoop on(String event, Consumer<String> listener) {
    listeners.put(event, listener);
    return this;
  }

  void emit(String name, String data) {
    events.add(new Event(name, data));
  }

  void stop() {Thread.currentThread().interrupt();}

  void start() {
    while (!events.isEmpty() || !Thread.interrupted()) {
      if (events.isEmpty()) continue;
      var event = events.pop();
      listeners.getOrDefault(event.name, ignored -> {}).accept(event.data);
    }
  }
}

public class VertxDemo1 {
  static void timeout(long mills, Runnable runnable) {
    try {
      Thread.sleep(mills);
      runnable.run();
    } catch (InterruptedException ignored) {}
  }

  static void nop() {}

  public static void main(String[] args) {
    final var eventLoop = new EventLoop();

    new Thread(() -> {
      range(0, 6)
        .forEach(value -> timeout(1000, () -> eventLoop.emit("tick", STR. "Tick #\{ value }" )));
      timeout(500, () -> eventLoop.emit("stop", "EventLoop stopped"));
    }).start();

    new Thread(() -> range(0, 1)
      .peek(value -> timeout(2500, () -> eventLoop.emit("hello", STR. "Hello World n.\{ value }" )))
      .forEach(value -> timeout(800, () -> eventLoop.emit("hello", STR. "Hello Universe n.\{ value }" )))
    ).start();

    eventLoop.emit("hello", "mars");
    eventLoop.emit("foo", "bar");

    eventLoop
      .on("hello", out::println)
      .on("tick", out::println)
      .on("stop", value -> {
        out.println(value);
        eventLoop.stop();
      })
      .start();

    out.println("EventLoop demo terminated");
  }
}

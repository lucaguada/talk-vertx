import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.lang.System.out;
import static java.util.stream.IntStream.range;

void timeout(long mills, Runnable runnable) {
  try {
    Thread.sleep(mills);
    runnable.run();
  } catch (InterruptedException ignored) {}
}

void main(String[] args) {
  final var eventLoop = new EventLoop();

  try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.execute(() -> {
      range(1, 7).forEach(n -> timeout(1000, () -> eventLoop.emit("tick", STR."Tick #\{n}")));
      timeout(1500, () -> eventLoop.emit("stop", "EventLoop stopped"));
    });

    executor.execute(() -> range(1, 3)
      .peek(n -> timeout(2500, () -> eventLoop.emit("hello", STR."Hello World n.\{n}")))
      .forEach(n -> timeout(1000, () -> eventLoop.emit("hello", STR."Hello Universe n.\{n}")))
    );

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

record EventLoop(Deque<Event> events, Map<String, Consumer<String>> listeners) {
  EventLoop() {this(new ConcurrentLinkedDeque<>(), new ConcurrentHashMap<>());}

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

  record Event(String name, String data) {}
}

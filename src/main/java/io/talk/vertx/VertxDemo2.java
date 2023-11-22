package io.talk.vertx;

import io.vertx.core.Vertx;

import static java.util.stream.IntStream.range;

interface VertxDemo2 {
  static void timeout(long mills, Runnable runnable) {
    try {
      Thread.sleep(mills);
      runnable.run();
    } catch (InterruptedException ignored) {}
  }

  static void main(String[] args) {
    var vertx = Vertx.vertx();
    var eventBus = vertx.eventBus();

    eventBus.<String>consumer("hello", message -> System.out.println(message.body()));
    eventBus.<String>consumer("tick", message -> System.out.println(message.body()));
    eventBus.<String>consumer("stop", message ->  { System.out.println(message.body()); vertx.close();});

    range(0, 6)
      .peek(n -> vertx.setTimer(1000, it -> eventBus.publish("tick", STR. "Tick #\{ n }" )))
      .forEach(n -> {
        if (n == 5) vertx.setTimer(n * 1000 + 500, it -> eventBus.publish("stop", "EventLoop stopped"));
      })
    ;

    range(0, 2)
      .peek(n -> vertx.setTimer(2500, it -> eventBus.publish("hello", STR. "Hello World n.\{ n }" )))
      .forEach(n -> vertx.setTimer(1000, it -> eventBus.publish("hello", STR. "Hello Universe n.\{ n }" )));

    eventBus.publish("hello", "mars");
    eventBus.publish("foo", "bar");
  }
}

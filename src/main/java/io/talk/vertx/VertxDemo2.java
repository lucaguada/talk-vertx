import io.vertx.core.Vertx;

import static java.util.stream.IntStream.range;


void main(String[] args) {
  var vertx = Vertx.vertx();
  var eventBus = vertx.eventBus();

  eventBus.<String>consumer("hello", message -> System.out.println(message.body()));
  eventBus.<String>consumer("tick", message -> System.out.println(message.body()));
  eventBus.<String>consumer("stop", message -> {
    System.out.println(message.body());
    vertx.close();
  });

  range(1, 7)
    .peek(n -> vertx.setTimer(n * 1000L, it -> eventBus.publish("tick", STR."Tick #\{n}")))
    .forEach(n -> {
      if (n == 5) vertx.setTimer(n * 1000L, it -> eventBus.publish("stop", "EventLoop stopped"));
    })
  ;

  range(1, 3)
    .peek(n -> vertx.setTimer(n * 2500L, it -> eventBus.publish("hello", STR."Hello World n.\{n}")))
    .forEach(n -> vertx.setTimer(n * 3000L, it -> eventBus.publish("hello", STR."Hello Universe n.\{n}")));

  eventBus.publish("hello", "mars");
  eventBus.publish("foo", "bar");
}

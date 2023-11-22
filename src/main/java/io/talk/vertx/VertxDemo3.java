import io.vertx.core.Vertx;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

void main() {
  var vertx = Vertx.vertx();
  var eventBus = vertx.eventBus();

  var random = new SecureRandom();

  eventBus.<String>consumer("greeting").handler(message -> {
    if (message.body().equals("1"))
      message.reply(STR."hello world \{message.body()}");
    else
      message.fail(400, STR."bye underworld \{message.body()}");
  });

  String message = STR."\{random.nextInt(2)}";
  eventBus.<String>request("greeting", message , async -> {
    if (async.succeeded()) {
      System.out.println(STR."Message: \{async.result().body()}\n");
    }
    else if (async.failed())
      System.err.println(STR."Error: \{async.cause().getMessage()}\n");
  });
}

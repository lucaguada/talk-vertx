
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import java.security.SecureRandom;

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

  printMessage(eventBus, random)
    .onSuccess(message -> System.out.println(STR."Message: \{message}\n"))
    .onFailure(throwable -> System.err.println(STR."Error: \{throwable.getMessage()}\n"));
}

Future<String> printMessage(EventBus eventBus, SecureRandom random) {
  var promise = Promise.<String>promise();
  String message = STR."\{random.nextInt(2)}";
  eventBus.<String>request("greeting", message , async -> {
    if (async.succeeded())
      promise.complete(async.result().body());

    else if (async.failed())
      promise.fail(async.cause().getMessage());
  });

  return promise.future();
}



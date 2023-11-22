import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

import java.security.SecureRandom;

import static io.vertx.core.Future.succeededFuture;

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

  eventBus.<String>request("greeting", STR."\{random.nextInt(2)}")
    .map(Message::body)
    .recover(error -> succeededFuture("hello recovered world"))
    .onSuccess(message -> System.out.println(STR."Message: \{message}\n"))
    .onFailure(throwable -> System.err.println(STR."Error: \{throwable.getMessage()}\n"));
}



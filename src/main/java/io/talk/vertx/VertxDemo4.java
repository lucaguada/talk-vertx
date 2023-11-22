import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.Message;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static io.vertx.core.Future.succeededFuture;

void main() {
  var vertx = Vertx.vertx();

  vertx.deployVerticle(new Requestor())
    .onSuccess(it -> System.out.println("Verticle Requestor caricato"));
  vertx.deployVerticle(new Replier())
    .onSuccess(it -> System.out.println("Verticle Replier caricato"));

  vertx.deployVerticle(new ASe(), new DeploymentOptions().setWorker(true)).onSuccess(it -> System.out.println("A Se"));
}

static final class ASe extends AbstractVerticle {
  @Override
  public void start() throws Exception {
    vertx.executeBlocking(() -> {
      int numeroCicli = 1000000000; // Modifica questo valore in base alle tue esigenze

      long startTime = System.currentTimeMillis();

      for (int i = 0; i < numeroCicli; i++) {
        // Esegui qualche operazione di calcolo pesante
        double risultato = Math.pow(Math.random(), Math.random()) * Math.sqrt(Math.random());
      }

      long endTime = System.currentTimeMillis();

      long tempoTotale = endTime - startTime;
      return tempoTotale;
    });
  }
}

static final class Requestor extends AbstractVerticle {
  @Override
  public void start() throws Exception {
    var random = new SecureRandom();

    vertx.eventBus()
      .<String>request("greeting", STR."\{random.nextInt(2)}")
      .map(Message::body)
      .recover(error -> succeededFuture("hello recovered world"))
      .onSuccess(message -> System.out.println(STR."Message: \{message}\n"))
      .onFailure(throwable -> System.err.println(STR."Error: \{throwable.getMessage()}\n"));
  }
}

@SuppressWarnings("InnerClassMayBeStatic")
final class Replier extends AbstractVerticle {
  @Override
  public void start() {
    vertx.eventBus().<String>consumer("greeting").handler(message -> {
      if (message.body().equals("1"))
        message.reply(STR."hello world \{message.body()}");
      else
        message.fail(400, STR."bye underworld \{message.body()}");
    });
  }
}



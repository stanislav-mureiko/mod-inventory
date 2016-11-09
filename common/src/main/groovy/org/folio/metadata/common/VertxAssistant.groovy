package org.folio.metadata.common

import io.vertx.groovy.core.Vertx
import java.util.concurrent.CompletableFuture

class VertxAssistant {

  private Vertx vertx

  def useVertx(Closure closure) {
    closure(vertx)
  }

  Vertx start() {
    if(vertx == null) {
      this.vertx = Vertx.vertx()
    }
  }

  void stop() {
    if (vertx != null) {
      def stopped = new CompletableFuture()

      vertx.close({ res ->
        if (res.succeeded()) {
          stopped.complete(null);
        } else {
          stopped.completeExceptionally(res.cause());
        }
      })

      stopped.join()
      vertx == null
    }
  }

  void deployGroovyVerticle(String verticleClass, CompletableFuture<String> deployed) {
    def startTime = System.currentTimeMillis()

    vertx.deployVerticle("groovy:" + verticleClass, ["worker": true ], { res ->
      if (res.succeeded()) {
        def elapsedTime = System.currentTimeMillis() - startTime
        println("${verticleClass} deployed in ${elapsedTime} milliseconds")
        deployed.complete(res.result());
      } else {
        deployed.completeExceptionally(res.cause());
      }
    });
  }

  void undeployVerticle(String deploymentId, CompletableFuture deployed) {
    vertx.undeploy(deploymentId, { res ->
      if (res.succeeded()) {
        deployed.complete();
      } else {
        deployed.completeExceptionally(res.cause());
      }
    });
  }
}

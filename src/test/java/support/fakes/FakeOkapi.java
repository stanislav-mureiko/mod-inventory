package support.fakes;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.util.ArrayList;

public class FakeOkapi extends AbstractVerticle {

  private static final String TENANT_ID = "test_tenant";
  private static final int PORT_TO_USE = 9493;
  private static final String address =
    String.format("http://localhost:%s", PORT_TO_USE);

  private HttpServer server;

  public static String getAddress() {
    return address;
  }

  public void start(Future<Void> startFuture) {
    System.out.println("Starting fake modules");

    Router router = Router.router(vertx);

    this.server = vertx.createHttpServer();

    registerFakeInstanceStorageModule(router);
    registerFakeHoldingStorageModule(router);
    registerFakeItemsModule(router);
    registerFakeMaterialTypesModule(router);
    registerFakeLoanTypesModule(router);
    registerFakeShelfLocationsModule(router);
    registerFakeInstanceTypesModule(router);
    registerFakeIdentifierTypesModule(router);
    registerFakeContributorNameTypesModule(router);

    server.requestHandler(router::accept)
      .listen(PORT_TO_USE, result -> {
        if (result.succeeded()) {
          System.out.println(
            String.format("Fake Okapi listening on %s", server.actualPort()));
          startFuture.complete();
        } else {
          startFuture.fail(result.cause());
        }
      });
  }

  public void stop(Future<Void> stopFuture) {
    System.out.println("Stopping fake modules");

    if(server != null) {
      server.close(result -> {
        if (result.succeeded()) {
          System.out.println(
            String.format("Stopped listening on %s", server.actualPort()));
          stopFuture.complete();
        } else {
          stopFuture.fail(result.cause());
        }
      });
    }
  }

  private void registerFakeInstanceStorageModule(Router router) {
    ArrayList<String> requiredProperties = new ArrayList<>();

    requiredProperties.add("title");
    requiredProperties.add("source");
    requiredProperties.add("instanceTypeId");
    requiredProperties.add("contributors");

    new FakeStorageModule("/instance-storage/instances", "instances",
      TENANT_ID, requiredProperties).register(router);
  }

  private void registerFakeHoldingStorageModule(Router router) {
    ArrayList<String> requiredProperties = new ArrayList<>();

    requiredProperties.add("instanceId");
    requiredProperties.add("permanentLocationId");

    new FakeStorageModule("/holdings-storage/holdings", "holdingsRecords",
      TENANT_ID, requiredProperties).register(router);
  }

  private void registerFakeItemsModule(Router router) {
    ArrayList<String> requiredProperties = new ArrayList<>();

    requiredProperties.add("materialTypeId");
    requiredProperties.add("permanentLoanTypeId");

    new FakeStorageModule("/item-storage/items", "items",
      TENANT_ID, requiredProperties).register(router);
  }

  private void registerFakeMaterialTypesModule(Router router) {
    registerFakeModule(router, "/material-types", "mtypes");
  }

  private void registerFakeLoanTypesModule(Router router) {
    registerFakeModule(router, "/loan-types", "loantypes");
  }

  private void registerFakeShelfLocationsModule(Router router) {
    registerFakeModule(router, "/shelf-locations", "shelflocations");
  }

  private void registerFakeIdentifierTypesModule(Router router) {
    registerFakeModule(router, "/identifier-types", "identifierTypes");
  }

  private void registerFakeInstanceTypesModule(Router router) {
    registerFakeModule(router, "/instance-types", "instanceTypes");
  }

  private void registerFakeContributorNameTypesModule(Router router) {
    registerFakeModule(router, "/contributor-name-types", "contributorNameTypes");
  }

  private void registerFakeModule(
    Router router,
    String rootPath,
    String collectionPropertyName) {

    new FakeStorageModule(rootPath, collectionPropertyName,
      TENANT_ID).register(router);
  }
}

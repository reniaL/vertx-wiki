package io.vertx.guides.wiki;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.guides.wiki.database.WikiDatabaseVerticle;

public class MainVerticle extends AbstractVerticle {
    
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        
        // deploy by new instance
        Future<String> dbVerticleDeployment = Future.future();  // <1>
        vertx.deployVerticle(new WikiDatabaseVerticle(), dbVerticleDeployment.completer());  // <2>
        
        dbVerticleDeployment.compose(id -> {  // <3>
            
            // deploy by full-qualified class name
            Future<String> httpVerticleDeployment = Future.future();
            vertx.deployVerticle(
                    "io.vertx.guides.wiki.http.HttpServerVerticle",  // <4>
                    new DeploymentOptions().setInstances(2),    // <5>
                    httpVerticleDeployment.completer());
            
            return httpVerticleDeployment;  // <6>
            
        }).setHandler(ar -> {   // <7>
            if (ar.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
            }
        });
    }
}

package io.quarkus.ts.external.applications;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.OpenShiftScenario;
import io.quarkus.test.scenarios.annotations.DisabledOnQuarkusSnapshot;
import io.quarkus.test.services.GitRepositoryQuarkusApplication;

@DisabledOnQuarkusSnapshot(reason = "999-SNAPSHOT is not available in the Maven repositories in OpenShift")
@OpenShiftScenario
public class OpenShiftTodoDemoIT {
    @GitRepositoryQuarkusApplication(repo = "https://github.com/quarkusio/todo-demo-app.git", mavenArgs = "-Dquarkus.package.type=uber-jar -DskipTests=true -Dquarkus.platform.artifact-id=quarkus-bom -Dquarkus.platform.version=${QUARKUS_VERSION} -Dquarkus.platform.group-id=com.redhat.quarkus.platform")
    static final RestService app = new RestService();

    @Test
    public void verify() {
        app.given()
                .get()
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}

package io.quarkus.ts.http.jaxrs.reactive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Objects;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.services.QuarkusApplication;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@Tag("QUARKUS-2487")
@QuarkusScenario
public class CompressionHandlerIT {

    @QuarkusApplication
    static RestService app = new RestService();

    private static final String BASE_PATH = "/compression/small";
    private static final Map<String, String> DEFAULT_SMALL_GZIP_ENDPOINTS = Map.of(
            BASE_PATH + "/default_compression_text_plain", MediaType.TEXT_PLAIN,
            BASE_PATH + "/default_compression_text_html", MediaType.TEXT_HTML,
            BASE_PATH + "/default_compression_text_xml", MediaType.TEXT_XML,
            BASE_PATH + "/default_compression_text_css", "text/css",
            BASE_PATH + "/default_compression_text_js", "text/javascript",
            BASE_PATH + "/default_compression_app_js", "application/javascript");

    private static final Map<String, String> DEFAULT_SMALL_NO_GZIP_ENDPOINTS = Map.of(
            BASE_PATH + "/default_no_compression_json", MediaType.APPLICATION_JSON,
            BASE_PATH + "/default_no_compression_xml", MediaType.APPLICATION_XML,
            BASE_PATH + "/default_no_compression_form_data", MediaType.MULTIPART_FORM_DATA);

    @Test
    public void defaultCompression() {
        for (var entry : DEFAULT_SMALL_GZIP_ENDPOINTS.entrySet()) {
            assertCompressed(entry.getKey(), entry.getValue());
        }
    }

    @Test
    public void defaultNoCompression() {
        for (var entry : DEFAULT_SMALL_NO_GZIP_ENDPOINTS.entrySet()) {
            assertUnCompressed(entry.getKey(), entry.getValue());
        }
    }

    @Test
    public void forceCompression() {
        assertCompressed(BASE_PATH + "/compression_custom_type", "application/x-custom-type");
    }

    @Test
    public void forceNoCompression() {
        assertUnCompressed(BASE_PATH + "/force_no_compression", MediaType.TEXT_PLAIN);
    }

    @Test
    public void mixingTypes() {
        assertUnCompressed(BASE_PATH + "/mixing_types", MediaType.APPLICATION_JSON);
        assertUnCompressed(BASE_PATH + "/mixing_types", MediaType.TEXT_PLAIN);
    }

    private void assertCompressed(String path, String ContentType) {
        String bodyStr = app.given()
                .when()
                .contentType(ContentType)
                .get(path).then().statusCode(200)
                .header("Content-Encoding", "gzip")
                .extract().asString();

        assertEquals(CompressionResource.SMALL_MESSAGE, bodyStr);
    }

    private void assertUnCompressed(String path, String ContentType) {
        ExtractableResponse<Response> response = app.given().when().contentType(ContentType)
                .get(path).then().statusCode(200)
                .extract();

        assertTrue(Objects.isNull(response.header("Content-Encoding")), response.headers().toString());
        assertEquals(CompressionResource.SMALL_MESSAGE, response.asString());
    }
}

package uk.co.whitbread.employee.bulk.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import uk.co.whitbread.employee.bulk.exception.GetEmployeeCSVListException;
import uk.co.whitbread.employee.bulk.service.CdhBulkEmployeeService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class BulkEmployeeControllerTest {
    private static final String COMPANY_ID = "companyId";
    private static final String BULK_UPLOAD_EMPLOYEES_SPREADSHEET_PATH = "src/test/resources/__files/employees_with-data.xlsx";
    public static final String LANGUAGE_PARAM = "language";
    public static final String LANGUAGE_DE = "de";
    public static final String COUNTRY_PARAM = "country";
    public static final String COUNTRY_DE = "de";
    private static final String AUTHORIZATION = "authorization jwt token";
    private static final String AUTHORIZATION_PARAM = "Authorization";

    @LocalServerPort
    private int serverPort;

    @MockitoBean
    private CdhBulkEmployeeService cdhBulkEmployeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private HttpClient httpClient;

    @BeforeEach
    void setup() {
        httpClient = HttpClient.newHttpClient();
    }

    private String baseUrl() {
        return "http://localhost:" + serverPort;
    }

    @Test
    void getEmployeeList_shouldReturnHttpOK() throws Exception {
        when(cdhBulkEmployeeService.bulkGetEmployees(anyString(), anyString()))
                .thenReturn(new ByteArrayOutputStream());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID + "/employees/bulk"))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION_PARAM, AUTHORIZATION)
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    }

    @Test
    void bulkAddEmployees_shouldReturn201Created() throws Exception {
        File file = new File(BULK_UPLOAD_EMPLOYEES_SPREADSHEET_PATH);
        
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
        
        String multipartBody = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"upFile\"; filename=\"" + file.getName() + "\"\r\n" +
                "Content-Type: application/octet-stream\r\n\r\n" +
                new String(fileBytes, java.nio.charset.StandardCharsets.ISO_8859_1) + "\r\n" +
                "--" + boundary + "--\r\n";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID + "/employees/bulk"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofString(multipartBody, java.nio.charset.StandardCharsets.ISO_8859_1))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
    }

    @Test
    void bulkAddEmployees_shouldReturn201Created_innBusinessTrue() throws Exception {
        File file = new File(BULK_UPLOAD_EMPLOYEES_SPREADSHEET_PATH);
        
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
        
        String multipartBody = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"upFile\"; filename=\"" + file.getName() + "\"\r\n" +
                "Content-Type: application/octet-stream\r\n\r\n" +
                new String(fileBytes, java.nio.charset.StandardCharsets.ISO_8859_1) + "\r\n" +
                "--" + boundary + "--\r\n";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID + "/employees/bulk?innBusiness=true"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofString(multipartBody, java.nio.charset.StandardCharsets.ISO_8859_1))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
    }

    @Test
    void bulkAddEmployees_shouldReturn201CreatedForGermanUsers() throws Exception {
        final File file = new File("src/test/resources/__files/employees_with-data.xls");
        final FileInputStream fileInputStream = new FileInputStream(file);

        final MockMultipartFile mockMultipartFile = new MockMultipartFile("employees",
                "employees_with-data.xls","multipart/form-data", fileInputStream);

        when(cdhBulkEmployeeService.bulkAddEmployees(AUTHORIZATION, COMPANY_ID,
                mockMultipartFile, LANGUAGE_DE, false))
                .thenReturn(true);
        
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
        
        String multipartBody = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"upFile\"; filename=\"" + file.getName() + "\"\r\n" +
                "Content-Type: application/octet-stream\r\n\r\n" +
                new String(fileBytes, java.nio.charset.StandardCharsets.ISO_8859_1) + "\r\n" +
                "--" + boundary + "--\r\n";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID + "/employees/bulk"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header(LANGUAGE_PARAM, LANGUAGE_DE)
                .header(COUNTRY_PARAM, COUNTRY_DE)
                .header(AUTHORIZATION_PARAM, AUTHORIZATION)
                .POST(HttpRequest.BodyPublishers.ofString(multipartBody, java.nio.charset.StandardCharsets.ISO_8859_1))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);

        Mockito.verify(cdhBulkEmployeeService).bulkAddEmployees(eq(AUTHORIZATION), eq(COMPANY_ID),
                any(MultipartFile.class), eq(LANGUAGE_DE), eq(false));
    }

    @Test
    void getEmployeeList_error_shouldGetNOOKResponse() throws Exception {
        when(cdhBulkEmployeeService.bulkGetEmployees(anyString(), anyString()))
                .thenThrow(new GetEmployeeCSVListException("errorMessage"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID + "/employees/bulk"))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION_PARAM, AUTHORIZATION)
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        
        var responseBody = objectMapper.readTree(response.body());
        assertThat(responseBody.get("details").size()).isEqualTo(1);
        assertThat(responseBody.get("details").get(0).asText()).isEqualTo("errorMessage");
    }

    @Test
    void getEmployeeList_shouldHandleInternalServerError() throws Exception {
        when(cdhBulkEmployeeService.bulkGetEmployees(anyString(), anyString()))
                .thenThrow(new RuntimeException("Internal Exception"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID + "/employees/bulk"))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION_PARAM, AUTHORIZATION)
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        
        var responseBody = objectMapper.readTree(response.body());
        assertThat(responseBody.get("code").asText()).isEqualTo("999");
        assertThat(responseBody.get("details").size()).isEqualTo(1);
        assertThat(responseBody.get("details").get(0).asText()).isEqualTo("Internal Exception");
    }
}

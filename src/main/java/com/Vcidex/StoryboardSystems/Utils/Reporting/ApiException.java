package com.Vcidex.StoryboardSystems.Utils.Reporting;

public class ApiException extends Exception {

    private final String requestBody;
    private final String responseBody;

    // ✅ Constructor using super(message) from Exception class
    public ApiException(String message, String requestBody, String responseBody) {
        super(message); // This calls the parent class (Exception) constructor
        this.requestBody = requestBody;
        this.responseBody = responseBody;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
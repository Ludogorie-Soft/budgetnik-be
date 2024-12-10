package com.ludogorieSoft.budgetnik.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ludogorieSoft.budgetnik.dto.response.ExceptionResponse;
import com.ludogorieSoft.budgetnik.exception.ApiException;
import com.ludogorieSoft.budgetnik.exception.ApiExceptionParser;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;

public class ObjectMapperHelper {
    public static void writeExceptionToObjectMapper(
            ObjectMapper objectMapper,
            ApiException exception,
            HttpServletResponse httpServletResponse
    ) throws IOException {
        ExceptionResponse exceptionResponse = ApiExceptionParser.parseException(exception);
        httpServletResponse.setStatus(exceptionResponse.getStatusCode());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ServletOutputStream out = httpServletResponse.getOutputStream();
        objectMapper.writeValue(out, exceptionResponse);
    }
}

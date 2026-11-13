package citizenReport.com.citizenReport.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {
    private final ApiErrorWriter errorWriter;

    public ApiAccessDeniedHandler(ApiErrorWriter errorWriter) {
        this.errorWriter = errorWriter;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException, ServletException {
        errorWriter.write(request, response, HttpStatus.FORBIDDEN.value(),
                "Forbidden", "You do not have permission to perform this action.");
    }
}

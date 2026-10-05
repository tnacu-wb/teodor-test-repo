package uk.co.whitbread.shared.commons.logging.trace;

import brave.propagation.aws.AWSPropagation;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class TraceFilter implements Filter {

    private static final String AMZN_TRACE_ID_NAME = "x-amzn-trace-id";
    private static final String FLOW_CODE_HEADER = "WB-FLOW-CODE";
    private static final String FLOW_CODE = "flow-code";
    private static final String WB_SESSION_ID_HEADER = "WB-Session-Id";
    private static final String WB_SESSION_ID = "wb-session-id";
    private final Tracer tracer;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        var amznHeader = AWSPropagation.FACTORY.get().extractor((extractionPoint, key) -> {
            var httpServletRequest = (HttpServletRequest) extractionPoint;
            var header = httpServletRequest.getHeader(key);
            return header == null ? "" : header;
        }).extract(servletRequest);
        if (amznHeader != null && amznHeader.context() != null) {
            tracer.createBaggageInScope(AMZN_TRACE_ID_NAME, amznHeader.context().traceIdString());
        }

        var flowCode = ((HttpServletRequest) servletRequest).getHeader(FLOW_CODE_HEADER);
        if (StringUtils.hasLength(flowCode)) {
            tracer.createBaggageInScope(FLOW_CODE, flowCode);
        }

        var wbSessionId = ((HttpServletRequest) servletRequest).getHeader(WB_SESSION_ID_HEADER);
        if (StringUtils.hasLength(wbSessionId)) {
            tracer.createBaggageInScope(WB_SESSION_ID, wbSessionId);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}

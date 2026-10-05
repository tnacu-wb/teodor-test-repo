package uk.co.whitbread.bart.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import uk.co.whitbread.bart.common.ErrorDetails;

/**
 * Created by Abu-Taleb on 17/11/2016.
 */
//TODO Send this code to shared library
public class Utils {

    private final static Logger LOG = LoggerFactory.getLogger(Utils.class);

    /**
     * Validates if there's any errors coming from BART
     *
     * @param methodError
     * @param errorDetail
     * @return true if no methodError and empty/blank error detail
     */
    public static boolean validateErrors(String methodError, ErrorDetails errorDetail) {
        return (StringUtils.isBlank(methodError)) && (errorDetail == null || StringUtils.isBlank(errorDetail.getErrorMessage()));
    }

    public static ErrorDetails convertErrorDetails(Object errorDetails) {
        ErrorDetails commonErrorDetails = null;
        if (errorDetails != null) {
            commonErrorDetails = new ErrorDetails();
            BeanUtils.copyProperties(errorDetails,commonErrorDetails);
        }
        return commonErrorDetails;
    }

}

package de.tum.in.www1.artemis.web.rest.util;

import java.nio.charset.Charset;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(HeaderUtil.class);

    private HeaderUtil() {
    }

    public static HttpHeaders createAlert(String applicationName, String message, String param) {
        return io.github.jhipster.web.util.HeaderUtil.createAlert(applicationName, message, param);
    }

    public static HttpHeaders createEntityCreationAlert(String applicationName, boolean enableTranslation, String entityName, String param) {
        return io.github.jhipster.web.util.HeaderUtil.createEntityCreationAlert(applicationName, enableTranslation, entityName, param);
    }

    public static HttpHeaders createEntityUpdateAlert(String applicationName, boolean enableTranslation, String entityName, String param) {
        return io.github.jhipster.web.util.HeaderUtil.createEntityUpdateAlert(applicationName, enableTranslation, entityName, param);
    }

    public static HttpHeaders createEntityDeletionAlert(String applicationName, boolean enableTranslation, String entityName, String param) {
        return io.github.jhipster.web.util.HeaderUtil.createEntityDeletionAlert(applicationName, enableTranslation, entityName, param);
    }

    public static HttpHeaders createFailureAlert(String applicationName, boolean enableTranslation, String entityName, String errorKey, String defaultMessage) {
        HttpHeaders headers = io.github.jhipster.web.util.HeaderUtil.createFailureAlert(applicationName, enableTranslation, entityName, errorKey, defaultMessage);
        headers.add("X-" + applicationName + "-message", defaultMessage);
        return headers;
    }

    /**
     * Creates a authorization headers for a given username and password
     * @param username the username
     * @param password the password
     * @return the acceptHeader
     */
    public static HttpHeaders createAuthorization(String username, String password) {
        HttpHeaders acceptHeaders = new HttpHeaders() {

            {
                set(com.google.common.net.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
                set(com.google.common.net.HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
            }
        };
        String authorization = username + ":" + password;
        String basic = new String(Base64.getEncoder().encode(authorization.getBytes(Charset.forName("UTF-8"))));
        acceptHeaders.set("Authorization", "Basic " + basic);

        return acceptHeaders;
    }
}

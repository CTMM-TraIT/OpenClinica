package org.akaza.openclinica.controller;

import com.auth0.*;
import org.akaza.openclinica.config.AppConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by yogi on 9/6/17.
 */
@Component
public class Auth0Controller {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private AuthenticationController controller;
    private final String userInfoAudience;
    public static final String RETURN_TO = "auth0_return_to";
    private String domain;

    @Autowired public Auth0Controller(AppConfig config) {
        domain = config.getDomain();
        userInfoAudience = String.format("https://%s/userinfo", domain);
    }
    public Tokens handle(HttpServletRequest request) throws IdentityVerificationException {
        return controller.handle(request);
    }

    public String getReturnTo(HttpServletRequest req) {
        return (String) SessionUtils.get(req, RETURN_TO);
    }

    public String buildAuthorizeUrl(HttpServletRequest request, boolean sso) {
        int port = request.getServerPort();
        String portStr ="";
        if (port != 80 && port != 443) {
            portStr = ":" + port;
        }
        String redirectUri = request.getScheme() + "://" + request.getServerName() + portStr + request.getContextPath() + "/pages/callback";
        return this.buildAuthorizeUrl(request, redirectUri, sso);
    }

    public String buildAuthorizeSignatureUrl(HttpServletRequest request, boolean sso) {
        int port = request.getServerPort();
        String portStr ="";
        if (port != 80 && port != 443) {
            portStr = ":" + port;
        }
        String redirectUri = request.getScheme() + "://" + request.getServerName() + portStr + request.getContextPath() + "/pages/signatureCallback";
        return this.buildAuthorizeUrl(request, redirectUri, sso);
    }

    public String setReturnTo(HttpServletRequest request) {
        return (String)SessionUtils.get(request, RETURN_TO);
    }

    public String buildAuthorizeUrl(HttpServletRequest request, String redirectUri, boolean sso) {
        AuthorizeUrl authUrl = controller
                .buildAuthorizeUrl(request, redirectUri)
                .withAudience(userInfoAudience);
        String returnTo = (String)SessionUtils.get(request, RETURN_TO);
        if (returnTo == null) {
            returnTo = request.getRequestURI();
            SessionUtils.set(request, RETURN_TO, returnTo);
        }

/* Add prompt=none if SSO is desired */
        if (sso) authUrl.withParameter("prompt", "none");
        else authUrl.withParameter("prompt", "login");
        authUrl.withAudience("https://www.openclinica.com");
        return authUrl.build();
    }

    public String buildLogoutURL (String redirectURI) {
        String encodedURL = "";
        try {
            encodedURL = URLEncoder.encode(redirectURI, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Bad redirect URI %s Error message %s", redirectURI, e.getMessage());
        }
        return String.format("https://%s/v2/logout/?returnTo=%s", domain, encodedURL);
    }
}
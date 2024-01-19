package io.github.gr3gdev.fenrir.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * HttpStatus.
 */
@RequiredArgsConstructor
@Getter
public enum HttpStatus {
    CONTINUE("100 Continue"),
    SWITCHING_PROTOCOLS("101 Switching Protocols"),
    PROCESSING("102 Processing"),
    EARLY_HINTS("103 Early Hints"),
    OK("200 OK"),
    CREATED("201 Created"),
    ACCEPTED("202 Accepted"),
    NON_AUTHORITATIVE_INFORMATION("203 Non-Authoritative Information"),
    NO_CONTENT("204 No Content"),
    RESET_CONTENT("205 Reset Content"),
    PARTIAL_CONTENT("206 Partial Content"),
    MULTI_STATUS("207 Multi-Status"),
    ALREADY_REPORTED("208 Already Reported"),
    CONTENT_DIFFERENT("210 Content Different"),
    IM_USED("226 IM Used"),
    MULTIPLE_CHOICES("300 Multiple Choices"),
    MOVED_PERMANENTLY("301 Moved Permanently"),
    FOUND("302 Found"),
    SEE_OTHER("303 See Other"),
    NOT_MODIFIED("304 Not Modified"),
    USE_PROXY("305 Use Proxy (depuis HTTP/1.1)"),
    SWITCH_PROXY("306 Switch Proxy"),
    TEMPORARY_REDIRECT("307 Temporary Redirect"),
    PERMANENT_REDIRECT("308 Permanent Redirect"),
    TOO_MANY_REDIRECTS("310 Too many Redirects"),
    BAD_REQUEST("400 Bad Request"),
    UNAUTHORIZED("401 Unauthorized"),
    PAYMENT_REQUIRED("402 Payment Required"),
    FORBIDDEN("403 Forbidden"),
    NOT_FOUND("404 Not Found"),
    METHOD_NOT_ALLOWED("405 Method Not Allowed"),
    NOT_ACCEPTABLE("406 Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED("407 Proxy Authentication Required"),
    REQUEST_TIMEOUT("408 Request Time-out"),
    CONFLICT("409 Conflict"),
    GONE("410 Gone"),
    LENGTH_REQUIRED("411 Length Required"),
    PRECONDITION_FAILED("412 Precondition Failed"),
    REQUEST_ENTITY_TOO_LARGE("413 Request Entity Too Large"),
    REQUEST_URI_TOO_LONG("414 Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE("415 Unsupported Media Type"),
    REQUESTED_RANGE_UNSATISFIABLE("416 Requested range unsatisfiable"),
    EXPECTATION_FAILED("417 Expectation failed"),
    IM_A_TEAPOT("418 I’m a teapot"),
    BAD_MAPPING__MISDIRECTED_REQUEST("421 Bad mapping / Misdirected Request"),
    UNPROCESSABLE_ENTITY("422 Unprocessable entity"),
    LOCKED("423 Locked"),
    METHOD_FAILURE("424 Method failure"),
    UNORDERED_COLLECTION("425 Unordered Collection"),
    UPGRADE_REQUIRED("426 Upgrade Required"),
    PRECONDITION_REQUIRED("428 Precondition Required"),
    TOO_MANY_REQUESTS("429 Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE("431 Request Header Fields Too Large"),
    RETRY_WITH("449 Retry With"),
    BLOCKED_BY_WINDOWS_PARENTAL_CONTROLS("450 Blocked by Windows Parental Controls"),
    UNAVAILABLE_FOR_LEGAL_REASONS("451 Unavailable For Legal Reasons"),
    UNRECOVERABLE_ERROR("456 Unrecoverable Error"),
    NO_RESPONSE("444 No Response"),
    SSL_CERTIFICATE_ERROR("495 SSL Certificate Error"),
    SSL_CERTIFICATE_REQUIRED("496 SSL Certificate Required"),
    HTTP_REQUEST_SENT_TO_HTTPS_PORT("497 HTTP Request Sent to HTTPS Port"),
    TOKEN_EXPIRED_OR_INVALID("498 Token expired/invalid"),
    CLIENT_CLOSED_REQUEST("499 Client Closed Request"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error"),
    NOT_IMPLEMENTED("501 Not Implemented"),
    BAD_GATEWAY_OU_PROXY_ERROR("502 Bad Gateway ou Proxy Error"),
    SERVICE_UNAVAILABLE("503 Service Unavailable"),
    GATEWAY_TIMEOUT("504 Gateway Time-out"),
    HTTP_VERSION_NOT_SUPPORTED("505 HTTP Version not supported"),
    VARIANT_ALSO_NEGOTIATES("506 Variant Also Negotiates"),
    INSUFFICIENT_STORAGE("507 Insufficient storage"),
    LOOP_DETECTED("508 Loop detected"),
    BANDWIDTH_LIMIT_EXCEEDED("509 Bandwidth Limit Exceeded"),
    NOT_EXTENDED("510 Not extended"),
    NETWORK_AUTHENTICATION_REQUIRED("511 Network authentication required"),
    UNKNOWN_ERROR("520 Unknown Error"),
    WEB_SERVER_IS_DOWN("521 Web Server Is Down"),
    CONNECTION_TIMED_OUT("522 Connection Timed Out"),
    ORIGIN_IS_UNREACHABLE("523 Origin Is Unreachable"),
    A_TIMEOUT_OCCURRED("524 A Timeout Occurred"),
    SSL_HANDSHAKE_FAILED("525 SSL Handshake Failed"),
    INVALID_SSL_CERTIFICATE("526 Invalid SSL Certificate"),
    RAILGUN_ERROR("527 Railgun Error");

    private final String code;
}
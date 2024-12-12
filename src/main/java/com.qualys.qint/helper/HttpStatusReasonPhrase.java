package com.qualys.qint.helper;

import org.apache.hc.core5.http.HttpStatus;

/**
 * @author jyadav@qualys.com
 */
public class HttpStatusReasonPhrase {

    public static String getReasonPhrase(int statusCode) {
        switch (statusCode) {
            case HttpStatus.SC_INFORMATIONAL:
                return "Informational";
            case HttpStatus.SC_SWITCHING_PROTOCOLS:
                return "Switching Protocols";
            case HttpStatus.SC_PROCESSING:
                return "Processing";
            case HttpStatus.SC_EARLY_HINTS:
                return "Early Hints";
            case HttpStatus.SC_OK:
                return "OK";
            case HttpStatus.SC_CREATED:
                return "Created";
            case HttpStatus.SC_ACCEPTED:
                return "Accepted";
            case HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION: // 301
                return "Non Authoritative Information";
            case HttpStatus.SC_NO_CONTENT:
                return "No Content";
            case HttpStatus.SC_RESET_CONTENT:
                return "Reset Content";
            case HttpStatus.SC_PARTIAL_CONTENT:
                return "Partial Content";
            case HttpStatus.SC_MULTI_STATUS:
                return "Multi Status";
            case HttpStatus.SC_ALREADY_REPORTED:
                return "Already Reported";
            case HttpStatus.SC_IM_USED:
                return "IM Used";
            case HttpStatus.SC_REDIRECTION:
                return "Redirection";
            case HttpStatus.SC_MOVED_PERMANENTLY:
                return "Moved Permanently";
            case HttpStatus.SC_MOVED_TEMPORARILY: // 301
                return "Moved Temporarily";
            case HttpStatus.SC_SEE_OTHER:
                return "See Other";
            case HttpStatus.SC_NOT_MODIFIED:
                return "Mot Modified";
            case HttpStatus.SC_USE_PROXY:
                return "Use Proxy";
            case HttpStatus.SC_TEMPORARY_REDIRECT:
                return "Temporary Redirect";
            case HttpStatus.SC_PERMANENT_REDIRECT:
                return "Permanent Redirect";
            case HttpStatus.SC_BAD_REQUEST:
                return "Bad Request";
            case HttpStatus.SC_UNAUTHORIZED:
                return "Unauthorized";
            case HttpStatus.SC_PAYMENT_REQUIRED:
                return "Payment Required";
            case HttpStatus.SC_FORBIDDEN: // 301
                return "Forbidden";
            case HttpStatus.SC_NOT_FOUND:
                return "Not Found";
            case HttpStatus.SC_METHOD_NOT_ALLOWED:
                return "Method Not Allowed";
            case HttpStatus.SC_NOT_ACCEPTABLE:
                return "Not Acceptable";
            case HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED:
                return "Proxy Authentication Required";
            case HttpStatus.SC_REQUEST_TIMEOUT:
                return "Request Timeout";
            case HttpStatus.SC_CONFLICT:
                return "Conflict";
            case HttpStatus.SC_GONE:
                return "Gone";
            case HttpStatus.SC_LENGTH_REQUIRED:
                return "Length Required";
            case HttpStatus.SC_PRECONDITION_FAILED: // 301
                return "Precondition Failed";
            case HttpStatus.SC_REQUEST_TOO_LONG:
                return "Request Too Long";
            case HttpStatus.SC_REQUEST_URI_TOO_LONG:
                return "Request URI Too Long";
            case HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE: // 301
                return "Unsupported Media Type";
            case HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE:
                return "Requested Range Not Satisfiable";
            case HttpStatus.SC_EXPECTATION_FAILED:
                return "Expectation Failed";
            case HttpStatus.SC_MISDIRECTED_REQUEST:
                return "Misdirected Request";
            case HttpStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE:
                return "Insufficient Space On Resource";
            case HttpStatus.SC_METHOD_FAILURE:
                return "Method Failure";
            case HttpStatus.SC_UNPROCESSABLE_ENTITY:
                return "Unprocessable Entity";
            case HttpStatus.SC_LOCKED:
                return "Locked";
            case HttpStatus.SC_FAILED_DEPENDENCY:
                return "Failed Dependency";
            case HttpStatus.SC_TOO_EARLY: // 301
                return "Too Early";
            case HttpStatus.SC_UPGRADE_REQUIRED:
                return "Upgrade Required";
            case HttpStatus.SC_PRECONDITION_REQUIRED:
                return "Precondition Required";
            case HttpStatus.SC_TOO_MANY_REQUESTS:
                return "Too Many Requests";
            case HttpStatus.SC_REQUEST_HEADER_FIELDS_TOO_LARGE:
                return "Request Header Fields Too Large";
            case HttpStatus.SC_UNAVAILABLE_FOR_LEGAL_REASONS:
                return "Unavailable For Legal Reasons";
            case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                return "Internal Server Error";
            case HttpStatus.SC_NOT_IMPLEMENTED:
                return "Not Implemented";
            case HttpStatus.SC_BAD_GATEWAY:
                return "Bad Gateway";
            case HttpStatus.SC_SERVICE_UNAVAILABLE:
                return "Service Unavailable";
            case HttpStatus.SC_GATEWAY_TIMEOUT:
                return "Gateway Timeout";
            case HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED:
                return "Http Version Not Supported";
            case HttpStatus.SC_VARIANT_ALSO_NEGOTIATES:
                return "variant Also Negotiates";
            case HttpStatus.SC_INSUFFICIENT_STORAGE:
                return "Insufficient Storage";
            case HttpStatus.SC_LOOP_DETECTED:
                return "Loop Detected";
            case HttpStatus.SC_NOT_EXTENDED:
                return "Not Extended";
            case HttpStatus.SC_NETWORK_AUTHENTICATION_REQUIRED:
                return "Network Authentication Required";
            default:
                return "Internal Server Error ";
        }
    }

}

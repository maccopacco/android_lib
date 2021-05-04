package com.maxdreher

/**
 * List of MIME types
 */
enum class MIME_TYPE(val value: String) {
    HTML("text/html"),
    HTML_ZIPPED("application/zipapplication/zip"),
    PLAIN_TEXT("text/plaintext/plain"),
    RICH_TEXT("application/rtf"),
    OPEN_OFFICE_DOC("application/vnd.oasis.opendocument.text"),
    PDF("application/pdfapplication/pdfapplication/pdfapplication/pdf"),
    MS_WORD_DOCUMENT("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    EPUB("application/epub+zip"),
    MS_EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    OPEN_OFFICE_SHEET("application/x-vnd.oasis.opendocument.spreadsheet"),
    CSV("text/csv"),
    JSON("application/json"),
    SHEET_ONLY("text/tab-separated-values"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    SVG("image/svg+xml"),
    MS_POWERPOINT("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    OPEN_OFFICE_PRESENTATION("application/vnd.oasis.opendocument.presentation"),
}
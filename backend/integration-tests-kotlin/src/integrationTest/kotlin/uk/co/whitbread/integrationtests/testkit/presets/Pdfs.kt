package uk.co.whitbread.integrationtests.testkit.presets

/**
 * Deterministic document fixtures for attachment scenarios.
 */
object Pdfs {
    /**
     * A minimal single-page PDF (catalog, page tree, one empty page, xref, trailer) encoded as
     * base64. It parses under PDFBox, so it passes the adapter's local PDF validation while
     * staying small enough to read in evidence.
     */
    const val MINIMAL_PDF_BASE64: String =
        "JVBERi0xLjQKMSAwIG9iago8PCAvVHlwZSAvQ2F0YWxvZyAvUGFnZXMgMiAwIFIgPj4KZW5kb2JqCjIgMCBvYmoK" +
            "PDwgL1R5cGUgL1BhZ2VzIC9LaWRzIFszIDAgUl0gL0NvdW50IDEgPj4KZW5kb2JqCjMgMCBvYmoKPDwgL1R5" +
            "cGUgL1BhZ2UgL1BhcmVudCAyIDAgUiAvTWVkaWFCb3ggWzAgMCA2MTIgNzkyXSA+PgplbmRvYmoKeHJlZgow" +
            "IDQKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDAwMDA5IDAwMDAwIG4gCjAwMDAwMDAwNTggMDAwMDAgbiAK" +
            "MDAwMDAwMDExNSAwMDAwMCBuIAp0cmFpbGVyCjw8IC9TaXplIDQgL1Jvb3QgMSAwIFIgPj4Kc3RhcnR4cmVm" +
            "CjE4NgolJUVPRgo="
}

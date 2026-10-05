package uk.co.whitbread.piba.account.model;

import java.io.ByteArrayOutputStream;

public record TransactionsFileResponse(String fileName, ByteArrayOutputStream filedata) {
}

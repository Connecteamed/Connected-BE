package com.connecteamed.server.domain.document.enums;

public enum DocumentFileType {
    PDF, DOCX, IMAGE, TEXT;

    public boolean isFile() {
        return this != TEXT;
    }
}

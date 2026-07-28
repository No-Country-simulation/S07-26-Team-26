package com.ghostload.api.outreach.application.port.out;

import java.util.List;

public interface ParseContactFilePort {

    List<ContactFileRow> parse(byte[] content);
}

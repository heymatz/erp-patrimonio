package com.erp.patrimonio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.application.App;

class AppTest {
    @Test
    void shouldReturnHelloMessage() {
        assertEquals("Hello, ERP Patrimonio!", App.getMessage());
    }
}

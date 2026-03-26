package com.portfolio.manager.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class ModelMapperConfigTest {

    @Test
    void shouldCreateConfiguredModelMapper() {
        ModelMapper modelMapper = new ModelMapperConfig().modelMapper();

        assertNotNull(modelMapper);

        Source source = new Source("Projeto");
        Target target = modelMapper.map(source, Target.class);

        assertEquals("Projeto", target.name);
    }

    private record Source(String name) {
    }

    private static class Target {
        private String name;
    }
}

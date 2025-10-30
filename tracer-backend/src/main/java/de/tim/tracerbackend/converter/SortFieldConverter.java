package de.tim.tracerbackend.converter;

import de.tim.tracerbackend.dto.SortField;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SortFieldConverter implements Converter<String, SortField> {

    @Override
    public SortField convert(String source) {
        return SortField.valueOf(source.toUpperCase());
    }
}

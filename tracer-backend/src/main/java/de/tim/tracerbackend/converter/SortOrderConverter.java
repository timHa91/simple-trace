package de.tim.tracerbackend.converter;

import de.tim.tracerbackend.dto.SortOrder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SortOrderConverter implements Converter<String, SortOrder> {

    @Override
    public SortOrder convert(String source) {
        return SortOrder.valueOf(source.toUpperCase());
    }
}

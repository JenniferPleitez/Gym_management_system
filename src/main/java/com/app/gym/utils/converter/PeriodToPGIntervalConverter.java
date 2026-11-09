package com.app.gym.utils.converter;

import java.time.Period;

import org.postgresql.util.PGInterval;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true) 
public class PeriodToPGIntervalConverter implements AttributeConverter<Period, PGInterval> {

    @Override
    public PGInterval convertToDatabaseColumn(Period period) {
        if (period == null) {
            return null;
        }
        // Convierte Period a PGInterval (años, meses, días, horas, minutos, segundos)
        return new PGInterval(period.getYears(), period.getMonths(), period.getDays(), 0, 0, 0);
    }

    @Override
    public Period convertToEntityAttribute(PGInterval dbData) {
        if (dbData == null) {
            return null;
        }
        // Convierte PGInterval a Period
        int years = dbData.getYears();
        int months = dbData.getMonths();
        int days = dbData.getDays();
        return Period.of(years, months, days);
    }
}
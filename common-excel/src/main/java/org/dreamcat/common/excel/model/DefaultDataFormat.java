package org.dreamcat.common.excel.model;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.util.ReflectUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author Jerry Will
 * @version 2026-02-04
 */
@Getter
@Setter
public class DefaultDataFormat {

    private boolean disable;
    private String dateTime = "yyyy-MM-dd HH:mm:ss";
    private String date = "yyyy-MM-dd";
    private String time = "HH:mm:ss";

    public String getDataFormat(Object v) {
        if (v instanceof Date || v instanceof LocalDateTime) {
            return dateTime;
        } else if (v instanceof LocalDate) {
            return date;
        } else if (v instanceof LocalTime) {
            return time;
        } else {
            return null;
        }
    }

    public String getDataFormat(Class<?> clazz) {
        if (ReflectUtil.isAssignable(Date.class, clazz) || LocalDateTime.class.equals(clazz)) {
            return dateTime;
        } else if (LocalDate.class.equals(clazz)) {
            return date;
        } else if (LocalTime.class.equals(clazz)) {
            return time;
        } else {
            return null;
        }
    }
}
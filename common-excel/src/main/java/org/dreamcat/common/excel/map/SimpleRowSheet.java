package org.dreamcat.common.excel.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.excel.ExcelCell;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.content.ExcelUnionContent;
import org.dreamcat.common.util.BeanUtil;
import org.dreamcat.common.util.ReflectUtil;

/**
 * Create by tuke on 2021/5/29
 */
@Getter
@SuppressWarnings({"rawtypes", "unchecked"})
public class SimpleRowSheet implements IExcelSheet {

    @Setter
    private String name;
    private Object scheme;
    @Setter
    private Function<Object, List<?>> schemeConverter = BeanUtil::toList;

    public SimpleRowSheet(String name, Object scheme) {
        this.name = name;
        reset(scheme);
    }

    public void reset(Object scheme) {
        this.scheme = scheme;
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.computeCells().iterator();
    }

    public List<IExcelCell> computeCells() {
        List list;
        if (scheme instanceof IExcelSheet) {
            list = new ArrayList<>();
            for (IExcelCell cell : (IExcelSheet) scheme) {
                list.add(cell);
            }
        } else if (scheme instanceof List) {
            list = (List<?>) scheme;
        } else if (scheme instanceof Map) {
            list = new ArrayList(((Map) scheme).values());
        } else if (scheme instanceof Collection || scheme.getClass().isArray()) {
            list = ReflectUtil.castAsList(scheme);
        } else {
            list = schemeConverter.apply(scheme);
        }

        int size = list.size();
        if (size == 0) {
            throw new IllegalArgumentException("scheme is empty");
        }
        List<IExcelCell> cells = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Object o = list.get(i);
            IExcelCell cell;
            if (o instanceof IExcelCell) {
                cell = (IExcelCell) o;
            } else {
                cell = new ExcelCell(new ExcelUnionContent(o), 0, i);
            }
            cells.add(cell);
        }
        return cells;
    }

}

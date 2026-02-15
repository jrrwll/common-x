package org.dreamcat.common.excel.build;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreamcat.common.excel.ExcelWorkbook;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.IExcelWriteCallback;
import org.dreamcat.common.excel.build.MasterDetailSheet.ExtraField;
import org.dreamcat.common.excel.model.DefaultDataFormat;
import org.dreamcat.common.excel.model.DetailRow;
import org.dreamcat.common.excel.model.MasterDetailRow;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.util.ListUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Create by tuke on 2020/7/22
 */
@SuppressWarnings({"unchecked"})
public class ExcelBuilder {

    private final ExcelWorkbook<IExcelSheet> workbook = new ExcelWorkbook<>();

    private ExcelBuilder() {
    }

    public static ExcelBuilder build() {
        return new ExcelBuilder();
    }

    public void writeTo(File file) throws IOException {
        workbook.writeTo(file);
    }

    public void writeTo(OutputStream output) throws IOException {
        workbook.writeTo(output);
    }

    public ExcelBuilder addSheet(String sheetName, List<String> header, List<List<Object>> body) {
        return addSheet(sheet -> sheet
                .name(sheetName)
                .header(header)
                .body(body));
    }

    public ExcelBuilder addSheet(Consumer<TableSheetBuilder> builder) {
        TableSheetBuilder sheetBuilder = new TableSheetBuilder();
        builder.accept(sheetBuilder);
        return addSheet(sheetBuilder.build());
    }

    public <T> ExcelBuilder addSheet(Class<T> beanType, List<T> body) {
        return addSheet(beanType, sheet -> sheet
                .body(body));
    }

    public <T> ExcelBuilder addSheet(Class<T> beanType, Consumer<BeanSheetBuilder<T>> builder) {
        BeanSheetBuilder<T> sheetBuilder = new BeanSheetBuilder<>(beanType);
        builder.accept(sheetBuilder);
        return addSheet(sheetBuilder.build());
    }

    public <M, D> ExcelBuilder addSheet(Class<M> masterType, Class<D> detailType, Consumer<MasterDetailSheetBuilder<M, D>> builder) {
        MasterDetailSheetBuilder<M, D> sheetBuilder = new MasterDetailSheetBuilder<>(masterType, detailType);
        builder.accept(sheetBuilder);
        return addSheet(sheetBuilder.build());
    }

    public ExcelBuilder addSheet(String sheetName, IExcelSheet firstSheet, IExcelSheet... remainingSheets) {
        return addSheet(sheetName, ListUtil.asList(firstSheet, remainingSheets));
    }

    public ExcelBuilder addSheet(String sheetName, List<IExcelSheet> sheets) {
        CompositeSheet sheet = new CompositeSheet();
        sheet.setName(sheetName);
        sheet.setSheets(sheets);
        return addSheet(sheet);
    }

    public ExcelBuilder addSheet(IExcelSheet sheet) {
        workbook.addSheet(sheet);
        return this;
    }

    @Setter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static class TableSheetBuilder {

        ExcelStyle defaultStyle;
        ExcelStyle headerStyle;
        List<ExcelStyle> headerStyles;
        List<ExcelStyle> bodyStyles;
        DefaultDataFormat defaultDataFormat;

        String name;
        List<IExcelWriteCallback> writeCallbacks = new ArrayList<>();

        List<String> header;
        List<List<Object>> body;

        public TableSheetBuilder addWriteCallback(IExcelWriteCallback writeCallback) {
            this.writeCallbacks.add(writeCallback);
            return this;
        }

        public TableSheet build() {
            TableSheet sheet = new TableSheet();
            sheet.setDefaultStyle(defaultStyle);
            sheet.setHeaderStyle(headerStyle);
            sheet.setHeaderStyles(headerStyles);
            sheet.setBodyStyles(bodyStyles);
            if (defaultDataFormat != null) {
                sheet.setDefaultDataFormat(defaultDataFormat);
            }

            sheet.setName(name);
            sheet.getWriteCallbacks().addAll(writeCallbacks);
            sheet.setHeader(header);
            sheet.setBody(body);
            return sheet;
        }
    }

    @Setter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static class BeanSheetBuilder<T> {

        final Class<T> beanType;

        ExcelStyle defaultStyle;
        ExcelStyle headerStyle;
        ExcelStyle bodyStyle;
        DefaultDataFormat defaultDataFormat;

        String name;
        List<IExcelWriteCallback> writeCallbacks = new ArrayList<>();
        boolean headerless;

        List<T> body;

        public BeanSheetBuilder<T> addWriteCallback(IExcelWriteCallback writeCallback) {
            this.writeCallbacks.add(writeCallback);
            return this;
        }

        public BeanSheet<T> build() {
            BeanSheet<T> sheet = new BeanSheet<>(beanType);
            if (defaultStyle != null) {
                sheet.applyStyle(defaultStyle);
            }
            if (headerStyle != null) {
                sheet.applyHeaderStyle(headerStyle);
            }
            if (bodyStyle != null) {
                sheet.applyBodyStyle(bodyStyle);
            }
            if (defaultDataFormat != null) {
                sheet.setDefaultDataFormat(defaultDataFormat);
            }

            sheet.setName(name);
            sheet.getWriteCallbacks().addAll(writeCallbacks);
            sheet.setHeaderless(headerless);
            sheet.setBody(body);
            return sheet;
        }
    }

    @Setter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static class MasterDetailSheetBuilder<M, D> {

        final Class<M> masterType;
        final Class<D> detailType;

        ExcelStyle defaultStyle;
        ExcelStyle headerStyle;
        ExcelStyle bodyStyle;
        DefaultDataFormat defaultDataFormat;

        List<ExtraField> masterExtraFields;
        List<ExtraField> detailExtraFields;
        boolean detailSubheader;
        String detailSubheaderName;
        String masterExtraSubheader;
        String detailExtraSubheader;

        String name;
        List<IExcelWriteCallback> writeCallbacks = new ArrayList<>();

        boolean headerless;
        List<MasterDetailRow<M, D>> body;

        public MasterDetailSheetBuilder<M, D> addWriteCallback(IExcelWriteCallback writeCallback) {
            this.writeCallbacks.add(writeCallback);
            return this;
        }

        public MasterDetailSheetBuilder<M, D> body(List<M> body, Function<M, List<? extends D>> detailGetter) {
            this.body = body.stream()
                    .map(m -> MasterDetailRow.fromEntities(m, (List<D>) detailGetter.apply(m)))
                    .collect(Collectors.toList());
            return this;
        }

        public MasterDetailSheetBuilder<M, D> body(
                List<M> body, Function<M, List<? extends D>> detailGetter,
                Function<M, Map<String, Object>> masterExtraGetter, Function<D, Map<String, Object>> detailExtraGetter) {
            this.body = body.stream()
                    .map(master -> {
                        MasterDetailRow<M, D> mdr = new MasterDetailRow<>();
                        mdr.setMaster(master);
                        if (masterExtraGetter != null) {
                            mdr.setMasterExtra(masterExtraGetter.apply(master));
                        }
                        mdr.setDetails(detailGetter.apply(master).stream()
                                .map(detail -> {
                                    DetailRow<D> dr = new DetailRow<>();
                                    dr.setDetail(detail);
                                    if (detailExtraGetter != null) {
                                        dr.setDetailExtra(detailExtraGetter.apply(detail));
                                    }
                                    return dr;
                                })
                                .collect(Collectors.toList()));
                        return mdr;
                    })
                    .collect(Collectors.toList());
            return this;
        }

        public MasterDetailSheet<M, D> build() {
            MasterDetailSheet<M, D> sheet = new MasterDetailSheet<>(masterType, detailType);
            if (defaultStyle != null) {
                sheet.applyStyle(defaultStyle);
            }
            if (headerStyle != null) {
                sheet.applyHeaderStyle(headerStyle);
            }
            if (bodyStyle != null) {
                sheet.applyBodyStyle(bodyStyle);
            }
            if (defaultDataFormat != null) {
                sheet.setDefaultDataFormat(defaultDataFormat);
            }

            sheet.setMasterExtraFields(masterExtraFields);
            sheet.setDetailExtraFields(detailExtraFields);
            sheet.setDetailSubheader(detailSubheader);
            sheet.setDetailSubheaderName(detailSubheaderName);
            sheet.setMasterExtraSubheader(masterExtraSubheader);
            sheet.setDetailExtraSubheader(detailExtraSubheader);

            sheet.setName(name);
            sheet.getWriteCallbacks().addAll(writeCallbacks);
            sheet.setHeaderless(headerless);
            sheet.setBody(body);
            return sheet;
        }
    }
}

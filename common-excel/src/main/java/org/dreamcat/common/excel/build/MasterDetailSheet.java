package org.dreamcat.common.excel.build;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.Triple;
import org.dreamcat.common.excel.ExcelCell;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.annotation.ExcelType;
import org.dreamcat.common.excel.model.DetailRow;
import org.dreamcat.common.excel.model.MasterDetailRow;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.util.ObjectUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Create by tuke on 2021/2/22
 */
@Getter
@Setter
public class MasterDetailSheet<M, D> implements IExcelSheet {

    private String name;
    private boolean headerless;
    private List<MasterDetailRow<M, D>> body; // data

    private List<ExtraField> masterExtraFields = new ArrayList<>();
    private List<ExtraField> detailExtraFields = new ArrayList<>();
    private boolean detailSubheader;
    private String detailSubheaderName;
    private String masterExtraSubheader; // set means enabled
    private String detailExtraSubheader;

    private final ExcelType.Value masterType;
    private final ExcelType.Value detailType;

    public MasterDetailSheet(
            Class<M> masterClass, Class<D> detailClass) {
        this.masterType = ExcelType.Value.parse(masterClass);
        this.detailType = ExcelType.Value.parse(detailClass);

        this.name = masterType.getName();
        this.detailSubheaderName = detailType.getName();
    }

    @Getter
    @Setter
    public static class ExtraField {

        private String fieldName;
        private String header;
        private ExcelStyle style;
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }

    private class Iter extends RowBasedSheetIter<MasterDetailRow<M, D>> {

        Iter() {
            super(MasterDetailSheet.this.body);
        }

        @Override
        Iterator<IExcelCell> getHeaderCells() {
            if (headerless) return null;

            return MasterDetailSheet.this.getHeaderCells().iterator();
        }

        @Override
        Iterator<IExcelCell> getColumnCells(MasterDetailRow<M, D> row) {
            return MasterDetailSheet.this.getColumnCells(row).iterator();
        }
    }

    private List<IExcelCell> getHeaderCells() {
        Triple<List<ExcelCell>, Integer, Integer> masterTriple = masterType.getHeaderCells();
        List<ExcelCell> masterCells = masterTriple.first();
        int masterRowSpan = masterTriple.second(), masterColumnSpan = masterTriple.third();

        Triple<List<ExcelCell>, Integer, Integer> detailTriple = detailType.getHeaderCells();
        List<ExcelCell> detailCells = masterTriple.first();
        int detailRowSpan = detailTriple.second(), detailColumnSpan = detailTriple.third();

        List<ExcelCell> masterExtraCells = new ArrayList<>();
        List<ExcelCell> detailExtraCells = new ArrayList<>();
        ExcelCell detailSubheaderCell = null;

        // master
        boolean span2 = detailRowSpan == 2 || detailExtraSubheader != null;
        boolean span3 = span2 && detailSubheader;
        span2 = !span3 && (span2 || detailSubheader || masterRowSpan == 2);
        if (span3) {
            // compute span
            for (ExcelCell cell : masterCells) {
                if (cell.getRowIndex() == 0) {
                    cell.setRowSpan(cell.getRowSpan() + 1);
                } else {
                    cell.setRowIndex(2);
                }
            }
        } else if (span2 && masterRowSpan == 1) {
            for (ExcelCell cell : masterCells) {
                cell.setRowSpan(2);
            }
        }

        // master extra
        int columnOffset = masterColumnSpan;
        if (ObjectUtil.isNotEmpty(masterExtraFields)) {
            for (ExtraField extraField : masterExtraFields) {
                ExcelCell cell = new ExcelCell(extraField.getHeader(), 0, columnOffset);
                cell.setStyle(extraField.getStyle());
                masterExtraCells.add(cell);
                columnOffset++;

                // compute span
                if (span3) {
                    cell.setRowIndex(2);
                } else if (masterExtraSubheader != null) {
                    cell.setRowIndex(1);
                }
            }
            if (masterExtraSubheader != null) {
                ExcelCell cell = new ExcelCell(masterExtraSubheader, 0, masterColumnSpan);
                cell.setColumnSpan(masterExtraFields.size());
                if (span3) {
                    cell.setRowSpan(2);
                }
                masterExtraCells.add(cell);
            }
        }

        // detail
        for (ExcelCell cell : detailCells) {
            cell.setColumnIndex(cell.getColumnIndex() + columnOffset);

            // compute span
            if (span3) {
                cell.setRowIndex(cell.getRowIndex() + 1);
            } else if (span2 && detailRowSpan == 1 && !detailSubheader) {
                cell.setRowSpan(2);
            }
        }
        columnOffset += detailColumnSpan;

        // detail extra
        if (ObjectUtil.isNotEmpty(detailExtraFields)) {
            for (ExtraField extraField : detailExtraFields) {
                ExcelCell cell = new ExcelCell(extraField.getHeader(), 0, columnOffset);
                cell.setStyle(extraField.getStyle());
                detailExtraCells.add(cell);
                columnOffset++;

                // compute span
                if (span3) {
                    cell.setRowIndex(2);
                } else if (detailSubheader || detailExtraSubheader != null) {
                    cell.setRowIndex(1);
                }
            }
            if (detailExtraSubheader != null) {
                ExcelCell cell = new ExcelCell(detailExtraSubheader, 0, columnOffset);
                if (detailSubheader) {
                    cell.setRowIndex(1);
                }
                detailExtraCells.add(cell);
            }
        }

        if (detailSubheader) {
            int offset = masterColumnSpan;
            if (ObjectUtil.isNotEmpty(masterExtraFields)) {
                offset += masterExtraFields.size();
            }
            detailSubheaderCell = new ExcelCell(detailSubheaderName, 0, offset);
        }

        List<IExcelCell> headerCells = new ArrayList<>();
        headerCells.addAll(masterCells);
        headerCells.addAll(masterExtraCells);
        if (detailSubheaderCell != null) {
            headerCells.add(detailSubheaderCell);
        }
        headerCells.addAll(detailCells);
        headerCells.addAll(detailExtraCells);
        return headerCells;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<IExcelCell> getColumnCells(MasterDetailRow<M, D> row) {
        List<ExcelCell> masterCells = masterType.getColumnCells(row.getMaster());

        List<DetailRow<D>> details = row.getDetails();
        if (ObjectUtil.isEmpty(details)) {
            return (List) masterCells;
        }

        int rowSpan = details.size();
        for (ExcelCell masterCell : masterCells) {
            masterCell.setRowSpan(rowSpan);
        }
        List<IExcelCell> cells = null;

        int rowOffset = 0;
        for (DetailRow<D> detail : details) {
            List<ExcelCell> detailCells = detailType.getColumnCells(detail.getDetail());
            for (ExcelCell cell : detailCells) {
                cell.setRowIndex(cell.getRowIndex() + rowOffset);
            }
            if (cells == null) {
                cells = new ArrayList<>(masterCells.size() + rowSpan * detailCells.size());
                cells.addAll(masterCells);
            }
            cells.addAll(detailCells);
            rowOffset++;
        }
        return cells;
    }
}

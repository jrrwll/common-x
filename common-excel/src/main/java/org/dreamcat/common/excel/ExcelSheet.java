package org.dreamcat.common.excel;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.dreamcat.common.excel.content.ExcelPicture;
import org.dreamcat.common.excel.content.ExcelPictureData;
import org.dreamcat.common.excel.content.IExcelContent;
import org.dreamcat.common.excel.style.ExcelComment;
import org.dreamcat.common.excel.style.ExcelHyperLink;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.util.ListUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Create by tuke on 2020/7/20
 * <p>
 * maximum-rows-and-columns-limits
 * XLS = 65536 * 256
 * XLSX = 1048576 * 16384
 */
@Data
@Slf4j
public class ExcelSheet implements IExcelSheet {

    private final String name;
    private final List<IExcelCell> cells;
    private final List<IExcelWriteCallback> writeCallbacks = new ArrayList<>();
    private final List<ExcelPicture> pictures = new ArrayList<>();

    public ExcelSheet(String name) {
        this.name = name;
        this.cells = new ArrayList<>();
    }

    public ExcelSheet(String name, List<IExcelCell> cells) {
        this(name);
        this.cells.addAll(cells);
    }

    public static ExcelSheet from(Sheet sheet, ExcelWorkbook<?> excelWorkbook) {
        ExcelSheet excelSheet = new ExcelSheet(sheet.getSheetName());

        Drawing<?> drawing = sheet.getDrawingPatriarch();
        if (drawing != null) {
            List<Picture> pictures = extractPictures(drawing);
            for (Picture picture: pictures) {
                PictureData pictureData = picture.getPictureData();
                String pictureDataId;
                if (pictureData instanceof XSSFPictureData) {
                    pictureDataId = ((XSSFPictureData) pictureData).getPackagePart().getPartName().getName();
                } else {
                    pictureDataId = String.valueOf(Arrays.hashCode(pictureData.getData()));
                }

                ExcelPictureData excelPictureData = new ExcelPictureData();
                excelPictureData.setId(pictureDataId);
                if (!excelWorkbook.pictureDatas.containsKey(excelPictureData)) {
                    log.error("undefined picture data {}: {}", pictureDataId, pictureData);
                    continue;
                }

                ExcelPicture excelPicture = new ExcelPicture(excelPictureData, picture);
                excelSheet.getPictures().add(excelPicture);
            }
        }

        int rowNum = sheet.getPhysicalNumberOfRows();
        Map<Integer, Map<Integer, ExcelCell>> cellMap = new TreeMap<>();
        for (int i = 0; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            int start = row.getFirstCellNum();
            if (start == -1) continue;
            int end = row.getLastCellNum();

            for (int j = start; j < end; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) continue;
                excelSheet.fillCellMap(cellMap, cell, i, j, excelWorkbook);
            }
        }

        excelSheet.computeSpans(cellMap, sheet);
        return excelSheet;
    }

    private void fillCellMap(
            Map<Integer, Map<Integer, ExcelCell>> cellMap,
            Cell cell, int i, int j, ExcelWorkbook<?> excelWorkbook) {
        IExcelContent content = IExcelContent.from(cell);
        ExcelCell excelCell = new ExcelCell(content, i, j);

        CellStyle style = cell.getCellStyle();
        Hyperlink hyperlink = cell.getHyperlink();
        Comment comment = cell.getCellComment();
        if (style != null) {
            ExcelStyle excelStyle = ListUtil.getOrNull(excelWorkbook.styles, style.getIndex());
            if (excelStyle == null) {
                log.error("undefined cell style: {}", ExcelStyle.from(style));
            } else {
                excelCell.setStyle(excelStyle);
            }
        }
        if (hyperlink != null) {
            excelCell.setHyperLink(ExcelHyperLink.from(hyperlink));
        }
        if (comment != null) {
            excelCell.setComment(ExcelComment.from(comment));
        }

        cells.add(excelCell);
        cellMap.computeIfAbsent(i, it -> new TreeMap<>())
                .put(j, excelCell);
    }

    private void computeSpans(
            Map<Integer, Map<Integer, ExcelCell>> cellMap, Sheet sheet) {
        int numMergedRegions = sheet.getNumMergedRegions();
        if (numMergedRegions == 0) return;

        for (IExcelCell cell : cells) {
            IExcelCell leftCell = getLeftCell(cell, cellMap);
            if (leftCell != null) {
                leftCell.setColumnSpan(cell.getColumnIndex() - leftCell.getColumnIndex());
            }
            IExcelCell topCell = getTopCell(cell, cellMap);
            if (topCell != null) {
                topCell.setRowSpan(cell.getRowIndex() - topCell.getRowIndex());
            }
        }

        // Note merge region for the last cell
        if (!cells.isEmpty()) {
            IExcelCell lastCell = cells.get(cells.size() - 1);
            CellRangeAddress addresses = sheet.getMergedRegion(numMergedRegions - 1);
            int ri = addresses.getFirstRow();
            int ci = addresses.getFirstColumn();
            if (lastCell.getRowIndex() == ri &&
                    lastCell.getColumnIndex() == ci) {
                lastCell.setRowSpan(addresses.getLastRow() - ri);
                lastCell.setColumnSpan(addresses.getLastColumn() - ci);
            }
        }
    }

    private static IExcelCell getLeftCell(IExcelCell cell,
            Map<Integer, Map<Integer, ExcelCell>> map) {
        int ri = cell.getRowIndex(), ci = cell.getColumnIndex();
        ExcelCell excelCell;
        while (--ci >= 0) {
            excelCell = map.getOrDefault(ri, Collections.emptyMap()).get(ci);
            if (excelCell != null) return excelCell;
        }
        return null;
    }

    private static IExcelCell getTopCell(IExcelCell cell,
            Map<Integer, Map<Integer, ExcelCell>> map) {
        int ri = cell.getRowIndex(), ci = cell.getColumnIndex();
        ExcelCell excelCell;
        while (--ri >= 0) {
            excelCell = map.getOrDefault(ri, Collections.emptyMap()).get(ci);
            if (excelCell != null) return excelCell;
        }
        return null;
    }

    private static List<Picture> extractPictures(Drawing<?> drawing) {
        List<Picture> pictures = new ArrayList<>();
        if (drawing instanceof XSSFDrawing) {
            XSSFDrawing xssfDrawing = (XSSFDrawing) drawing;
            List<XSSFShape> shapes = xssfDrawing.getShapes();
            for (XSSFShape shape : shapes) {
                if (shape instanceof XSSFPicture) {
                    long id = ((XSSFPicture)shape).getCTPicture().getNvPicPr().getCNvPr().getId();
                    pictures.add((XSSFPicture)shape);
                }
            }
        } else if (drawing instanceof SXSSFDrawing) {
            SXSSFDrawing sxssfDrawing = (SXSSFDrawing) drawing;
            for (XSSFShape shape : sxssfDrawing) {
                if (shape instanceof XSSFPicture) {
                    pictures.add((XSSFPicture)shape);
                }
            }
        } else if (drawing instanceof HSSFPatriarch){
            HSSFPatriarch hssfPatriarch = (HSSFPatriarch) drawing;
            for (HSSFShape shape : hssfPatriarch) {
                if (shape instanceof HSSFPicture) {
                    ((HSSFPicture)shape).getShapeId();
                    pictures.add((HSSFPicture)shape);
                }
            }
        }
        return pictures;
    }

    public void addCell(IExcelCell cell) {
        cells.add(cell);
    }

    public void addPicture(ExcelPicture picture) {
        pictures.add(picture);
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return cells.iterator();
    }

    public void addWriteCallback(IExcelWriteCallback writeCallback) {
        writeCallbacks.add(writeCallback);
    }
}

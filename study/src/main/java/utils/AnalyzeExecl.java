package utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class AnalyzeExecl {

    private static final String path2 = "D:\\导入测试0516\\导入测试_隐藏_行_列_sheet页\\0315隐藏行+列演_5行5列.xls";

    private final static String xls = "xls";

    private final static String xlsx = "xlsx";

    public static void main(String[] args) throws Exception {
        Position position = new Position();
        Workbook workbook = getWorkBook(path2);
        Sheet sheet = workbook.getSheetAt(0);
        Integer maxCellNum = getMaxCellIndex(sheet, position).getRight();
        Integer maxRowNumber = getMaxRowIndex(sheet,position);
        position = importXLS(sheet, maxCellNum, maxRowNumber, position);
        System.out.println(position);
        int up = position.getUp();
        int down = position.getDown();
        int left = position.getLeft();
        int right = position.getRight();
        List<String> data = getHeader(sheet,left,right,up);
        System.out.println(data);
        List<List<String>> a = new ArrayList<>();
        for (int i = up + 1 ;i<down;i ++){
            List<String> rowData = getData(sheet,left,right,i);
            Long count = rowData.stream().filter(e -> !StringUtils.isEmpty(e)).count();
            if (count > 0){
                a.add(rowData);
                System.out.println(rowData.toString() + rowData.size());
            }
        }
    }

    public static List<String> getHeader(Sheet sheet,int left,int right,int rowIndex){
        Row row = sheet.getRow(rowIndex);
        boolean hasMergedRegion = false;
        for (int i = left; i < right; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && ExcelUtils.isMergedRegion(sheet,cell.getRowIndex(),cell.getColumnIndex())){
                hasMergedRegion = true;
                break;
            }
        }
        List<String> values = new ArrayList<>();
        if (hasMergedRegion){
            for (int i = left; i < right; i++) {
                Cell cell = row.getCell(i);
                String cellValue = null;
                if (cell != null){
                    if (cell.getSheet().isColumnHidden(i)){
                        continue;
                    }
                    if (!ExcelUtils.isMergedRegion(sheet,cell.getRowIndex(),cell.getColumnIndex())){
                        Cell parentCell = sheet.getRow(cell.getRowIndex() -1).getCell(cell.getColumnIndex());
                        if (parentCell != null){
                            StringBuilder temp = new StringBuilder();
                            cellValue = getCellValue(sheet,parentCell);
                            String currentCellValue = cell != null ? getCellValue(sheet,cell) : null;
                            if (!StringUtils.isEmpty(cellValue) && !StringUtils.isEmpty(currentCellValue)){
                                cellValue = temp.append(cellValue.trim()).append(".").append(currentCellValue.trim()).toString();
                            }else {
                                cellValue = currentCellValue;
                            }
                        }else {
                            cellValue = getCellValue(sheet,cell);
                        }
                    }else {
                        cellValue = getMergedRegionValue(sheet,cell.getRowIndex(),cell.getColumnIndex());
                    }
                }
                values.add(cellValue);
            }
        }else {
            return getData(sheet,left,right,rowIndex);
        }
        return values;
    }

    public static List<String> getData(Sheet sheet,int left,int right,int rowIndex){
        Row row = sheet.getRow(rowIndex);
        List<String> values = new ArrayList<>();
        if (row.getZeroHeight()){
            return values;
        }
        for (int i = left; i < right; i++) {
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                if (cell.getSheet().isColumnHidden(i)) {
                    continue;
                } else {
                    cellValue = getCellValue(sheet, cell);
                }
                values.add(cellValue);
            }
        }
        return values;
    }

    public static Integer checkAllNull(Map<Integer, List<String>> map,int rowIndex,int lastRowNumber){
        if (rowIndex > lastRowNumber){
            return -1;
        }else {
            List<String> values = map.get(rowIndex);
            Long count = values.stream().filter(e -> !StringUtils.isEmpty(e)).count();
            if (count > 0) {
                return rowIndex;
            }else {
                rowIndex++;
                return checkAllNull(map,rowIndex,lastRowNumber);
            }
        }
    }

    public static Integer getMaxRowIndex(Sheet xSheet,Position position) throws Exception {
        Map<Integer, List<String>> map = getRowValue(xSheet);
        List<Integer> repeatZeroList = new ArrayList<>();
        Integer maxRowNumber = xSheet.getLastRowNum();
        Integer startRow = checkAllNull(map,0,maxRowNumber);
        position.setUp(startRow);
        for (int i = startRow; i < xSheet.getLastRowNum(); i++) {
            List<String> values = map.get(i);
            Long count = values.stream().filter(e -> !StringUtils.isEmpty(e)).count();
            if (count > 0) {
                if (!CollectionUtils.isEmpty(repeatZeroList)) {
                    repeatZeroList.clear();
                }
            } else {
                repeatZeroList.add(i);
            }
        }
        if (!CollectionUtils.isEmpty(repeatZeroList)) {
            maxRowNumber = repeatZeroList.get(0);
        }
        return maxRowNumber;
    }

    public static Position getMaxCellIndex(Sheet xSheet, Position position) throws Exception {
        Integer maxNum = getMaxLastCellNum(xSheet);
        Map<Integer, Map<Integer, Cell>> map = getRow(xSheet);
        Integer left = 0;
        boolean firstAppear = false;
        List<Integer> repeatZeroList = new ArrayList<>();
        for (int i = 0; i < maxNum; i++) {
            List<String> values = getVerticalValue(map, i);
            Long count = values.stream().filter(e -> !StringUtils.isEmpty(e)).count();
            if (count > 0) {
                if (!firstAppear) {
                    left = i;
                    firstAppear = true;
                }
                if (!CollectionUtils.isEmpty(repeatZeroList)) {
                    repeatZeroList.clear();
                }
            } else {
                repeatZeroList.add(i);
            }
        }
        Integer maxCellNumber = maxNum;
        if (!CollectionUtils.isEmpty(repeatZeroList)) {
            maxCellNumber = repeatZeroList.get(0);
        }
        position.setLeft(left);
        position.setRight(maxCellNumber);
        return position;
    }

    public static List<String> getVerticalValue(Map<Integer, Map<Integer, Cell>> map, int rowNum) {
        List<String> list = new ArrayList<>();
        Set<Integer> keySet = map.keySet();
        for (Integer key : keySet) {
            Cell xssfCell = map.get(key).get(rowNum);
            String value = getCellValue(xssfCell);
            list.add(value);
        }
        return list;
    }

    public static Map<Integer, Map<Integer, Cell>> getRow(Sheet sheet) {
        Map<Integer, Map<Integer, Cell>> map = new HashMap<>();
        for (int numRow = 0; numRow <= sheet.getLastRowNum(); numRow++) {
            Row xRow = sheet.getRow(numRow);
            Map<Integer, Cell> cellMap = getCell(xRow);
            map.put(numRow, cellMap);
        }
        return map;
    }

    public static Map<Integer, List<String>> getRowValue(Sheet sheet) {
        Map<Integer, List<String>> map = new HashMap<>();
        for (int numRow = 0; numRow <= sheet.getLastRowNum(); numRow++) {
            Row xRow = sheet.getRow(numRow);
            List<String> cellValueList = getCellValue(xRow);
            map.put(numRow, cellValueList);
        }
        return map;
    }

    public static Integer getMaxLastCellNum(Sheet xSheet) {
        Iterator<Row> iterator = xSheet.rowIterator();
        Integer maxNum = -1;
        while (iterator.hasNext()) {
            Row row = iterator.next();
            short num = row.getLastCellNum();
            if (num > maxNum) {
                maxNum = new Integer(num);
            }
        }
        return maxNum;
    }

    public static List<String> getCellValue(Row row) {
        List<String> stringList = new ArrayList<>();
        if (row != null) {
            for (int numCell = 0; numCell < row.getLastCellNum(); numCell++) {
                Cell xCell = (Cell) row.getCell(numCell);
                String value = getCellValue(xCell);
                stringList.add(value);
            }
        }
        return stringList;
    }

    public static Map<Integer, Cell> getCell(Row row) {
        Map<Integer, Cell> map = new HashMap<>();
        if (row != null) {
            Iterator<Cell> iterable = row.cellIterator();
            while (iterable.hasNext()) {
                Cell cell = iterable.next();
                map.put(cell.getColumnIndex(), cell);
            }
        }
        return map;
    }


    public static Position importXLS(Sheet xSheet, int maxCellNum, int maxRowNumber, Position position) throws Exception {
        List<List<Integer>> magicNums = new ArrayList<>();
        for (int numRow = 0; numRow <= maxRowNumber; numRow++) {
            Row xRow = xSheet.getRow(numRow);
            if (xRow == null) {
                continue;
            }
//            if(xRow.getZeroHeight()){
//                continue;
//            }
            //循环列cell
            List<Integer> magicNum = new ArrayList<>();
            for (int numCell = 0; numCell < maxCellNum; numCell++) {
                Cell xCell = xRow.getCell(numCell);
                if (xCell == null) {
                    magicNum.add(1);
                    continue;
                }
                if (ExcelUtils.isMergedRegion(xSheet, xCell.getRowIndex(), xCell.getColumnIndex())) {
                    if (ExcelUtils.isMergedRow(xSheet, xCell.getRowIndex(), xCell.getColumnIndex())) {
                        magicNum.add(0);
                    } else {
                        magicNum.add(1);
                    }
                } else {
                    magicNum.add(1);
                }
            }
            magicNums.add(magicNum);
        }

        for (int i = 0; i < magicNums.size(); i++) {
            print(magicNums.get(i));
        }
        Map<Integer, Wraped> map = getMap(magicNums,0.9f);
        Wraped wraped = null;
        int index = 0;
        if (map.size() > 0) {
            for (Integer num : map.keySet()) {
                Integer count = map.get(num).getCount();
                if (count > index) {
                    index = count;
                    wraped = map.get(num);
                }
            }
        }
        if (wraped == null){
            throw new Exception();
        }
        int startRow = position.getUp();
        if (startRow > wraped.getStart()){
            position.setUp(startRow);
        }else {
            position.setUp(wraped.getStart());
        }
        position.setDown(wraped.getEnd());
        return position;
    }

    public static Workbook getWorkBook(String path2) {
        String fileName = path2;
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream inputStream = new FileInputStream(path2);
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (fileName.endsWith(xls)) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (fileName.endsWith(xlsx)) {
                workbook = new XSSFWorkbook(inputStream);
            }
        } catch (IOException e) {
        }
        return workbook;
    }

    public static String getCellValue(Sheet sheet,Cell cell) {
        String cellValue = null;
        if (cell != null){
            if (ExcelUtils.isMergedRegion(sheet,cell.getRowIndex(),cell.getColumnIndex())){
                cellValue = getMergedRegionValue(sheet,cell.getRowIndex(),cell.getColumnIndex());
            }else {
                cellValue = getCellValue(cell);
            }
        }
        return cellValue;
    }

    /**
     * 获取合并单元格的值
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public static String getMergedRegionValue(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();   //获得该sheet所有合并单元格数量
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);    // 获得合并区域
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();


            //判断传入的单元格的行号列号是否在合并单元格的范围内，如果在合并单元格的范围内，择返回合并区域的首单元格格值
            if (row >= firstRow && row <= lastRow) {

                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
                }
            }
        }
        //如果该单元格行号列号不在任何一个合并区域，择返回null
        return null;
    }

    public static String getCellValue(Cell cell) {

        String cellValue = null;
        if (cell == null) {
            return cellValue;
        }
        if (cell.getSheet().isColumnHidden(cell.getColumnIndex())){
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                try {
                    cellValue = String.valueOf(cell.getStringCellValue());
                } catch (IllegalStateException e) {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            default:
                cellValue = null;
                break;
        }
        return cellValue;
    }

    public static Map<Integer, Wraped> getMap(List<List<Integer>> magicNums, float rate) {
        int allOne = 0;
        int parentIndex = 0;
        Map<Integer, Wraped> map = new HashMap<>();
        Wraped wraped = new Wraped(0, 0);
        for (int i = 0; i < magicNums.size(); i++) {
            List<Integer> nums = magicNums.get(i);
            Long number = nums.stream().filter(e -> e == 1).count();
            if (number.intValue() >= nums.size() * rate) {
                allOne++;
                wraped.setCount(allOne);
                if (i == magicNums.size() - 1) {
                    wraped.setEnd(i+1);
                    map.put(i, wraped);
                }
            } else {
                wraped.setEnd(i);
                map.put(i, wraped);
                parentIndex = i + 1;
                wraped = new Wraped(parentIndex, parentIndex);
                allOne = 0;
            }
        }
        return map;
    }

    public static void print(List<Integer> numbers) {
        for (int j = 0; j < numbers.size(); j++) {
            if (j == numbers.size() - 1) {
                System.out.println(numbers.get(j));
            } else {
                System.out.print(numbers.get(j));
            }
        }
    }

    public static class Wraped {

        private int start;

        private int end;

        private int count;

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public Wraped() {
        }

        public Wraped(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "Wraped{" +
                    "start=" + start +
                    ", end=" + end +
                    ", count=" + count +
                    '}';
        }
    }

    public static class Position {

        private int up;

        private int down;

        private int left;

        private int right;

        public int getUp() {
            return up;
        }

        public void setUp(int up) {
            this.up = up;
        }

        public int getDown() {
            return down;
        }

        public void setDown(int down) {
            this.down = down;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "up=" + up +
                    ", down=" + down +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }
}

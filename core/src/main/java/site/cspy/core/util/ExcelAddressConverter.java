package site.cspy.core.util;

public class ExcelAddressConverter {
    // 将列索引（从 0 开始）转换为 Excel 列字母
    public static String getExcelColumnName(int columnIndex) {
        StringBuilder columnName = new StringBuilder();
        columnIndex++; // Excel 列索引是从 1 开始的
        while (columnIndex > 0) {
            columnIndex--; // 处理从 A=1 开始的情况
            columnName.insert(0, (char) ('A' + (columnIndex % 26)));
            columnIndex /= 26;
        }
        return columnName.toString();
    }

    // 二维数组索引转换为 Excel 单元格地址
    public static String getExcelCellAddress(int rowIndex, int columnIndex) {
        return getExcelColumnName(columnIndex) + (rowIndex + 1); // Excel 行索引是从 1 开始的
    }

    // 批量转换整个二维数组
    public static String[][] convertArrayToExcelAddresses(int rows, int cols) {
        String[][] excelAddresses = new String[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                excelAddresses[r][c] = getExcelCellAddress(r, c);
            }
        }
        return excelAddresses;
    }
}

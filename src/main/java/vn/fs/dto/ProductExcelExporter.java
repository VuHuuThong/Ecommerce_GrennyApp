package vn.fs.dto;

import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.fs.entities.Order;
import vn.fs.entities.Product;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Data
public class ProductExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private List<Product> listProDetails;

    public ProductExcelExporter(List<Product> listProDetails) {
       this.listProDetails = listProDetails;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("ProductDetails");
    }

    private void writeHeaderRow() {

        Row row = sheet.createRow(0);

        Cell cell = row.createCell(0);
        cell.setCellValue("Tên Sản Phẩm");

        cell = row.createCell(1);
        cell.setCellValue("Đơn giá");

        cell = row.createCell(2);
        cell.setCellValue("Giảm giá");

        cell = row.createCell(3);
        cell.setCellValue("Số lượng");

    }

    private void writeDataRows() {
        int rowCount = 1;
        for (Product product : listProDetails ) {
            Row row = sheet.createRow(rowCount++);

            Cell cell = row.createCell(0);
            cell.setCellValue(product.getProductName());

            cell = row.createCell(1);
            cell.setCellValue(product.getPrice());

            cell = row.createCell(2);
            cell.setCellValue(product.getDiscount());

            cell = row.createCell(3);
            cell.setCellValue(product.getQuantity());

        }

    }

    public void export(HttpServletResponse response) throws IOException {

        writeHeaderRow();
        writeDataRows();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }
}

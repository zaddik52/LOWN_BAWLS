import fi.iki.elonen.NanoHTTPD;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class SimpleHttpServer extends NanoHTTPD {
    private static final String EXCEL_URL = "https://zaddik52.github.io/workflows/list_all.xlsx"; // עדכן את ה-URL הנכון

    public SimpleHttpServer() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("Server is running on port 8080...");
    }

    public static void main(String[] args) {
        try {
            new SimpleHttpServer();
        } catch (IOException e) {
            System.err.println("Couldn't start server:\n" + e);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (uri.equals("/")) {
            return newFixedLengthResponse(Response.Status.OK, "text/html", "<h1>Server is Running</h1>");
        }

        if (uri.startsWith("/getData")) {
            String idParam = session.getParms().get("id");
            if (idParam == null) {
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Missing 'id' parameter");
            }

            try {
                File excelFile = downloadExcel();
                String result = readExcel(excelFile, Integer.parseInt(idParam));
                return newFixedLengthResponse(Response.Status.OK, "text/plain", result);
            } catch (Exception e) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Error: " + e.getMessage());
            }
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 - Not Found");
    }

    private File downloadExcel() throws IOException {
        URL url = new URL(EXCEL_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            throw new IOException("Failed to download file: " + connection.getResponseMessage());
        }

        File tempFile = File.createTempFile("data", ".xlsx");
        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

    private String readExcel(File file, int id) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell idCell = row.getCell(0);
                if (idCell != null && idCell.getCellType() == CellType.NUMERIC && (int) idCell.getNumericCellValue() == id) {
                    Cell dataCell = row.getCell(1); // הנחה שהמידע נמצא בעמודה השנייה
                    return dataCell != null ? dataCell.toString() : "No data found";
                }
            }
        }
        return "ID not found";
    }
}

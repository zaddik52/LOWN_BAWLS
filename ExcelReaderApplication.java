import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class ExcelReaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExcelReaderApplication.class, args);
    }

    @GetMapping("/getData")
    public List<Map<String, String>> getData(@RequestParam List<Integer> numbers) throws IOException {
        String filePath = "path/to/your/list_all.xlsx";
        FileInputStream file = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        List<Map<String, String>> result = new ArrayList<>();

        for (Row row : sheet) {
            Cell numberCell = row.getCell(0);
            if (numberCell != null && numberCell.getCellType() == CellType.NUMERIC) {
                int number = (int) numberCell.getNumericCellValue();
                if (numbers.contains(number)) {
                    Map<String, String> data = new HashMap<>();
                    data.put("מספר", String.valueOf(number));
                    data.put("כינוי", row.getCell(1).getStringCellValue());
                    data.put("שם", row.getCell(2).getStringCellValue());
                    data.put("רמה", row.getCell(3).getStringCellValue());
                    result.add(data);
                }
            }
        }

        workbook.close();
        file.close();

        return result;
    }
}

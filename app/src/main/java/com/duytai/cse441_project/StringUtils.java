package com.duytai.cse441_project;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {

    // Phương thức để loại bỏ dấu tiếng Việt và xử lý các trường hợp đặc biệt
    public static String removeAccent(String input) {
        if (input == null) return null;

        // Chuẩn hóa chuỗi, chuyển thành dạng NFD (Normalization Form D)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // Tạo một biểu thức chính quy để loại bỏ tất cả các dấu kết hợp (diacritics) tiếng Việt
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

        // Thay thế ký tự "đ" và "Đ" thành "d" và "D"
        String result = pattern.matcher(normalized).replaceAll("").replace("đ", "d").replace("Đ", "D");

        // Đảm bảo không thay đổi các ký tự đặc biệt khác
        return result;
    }

    // Phương thức kiểm tra chuỗi chứa một từ khóa (không phân biệt dấu và chữ hoa)
    public static boolean containsIgnoreCaseAndAccent(String source, String keyword) {
        if (source == null || keyword == null) return false;

        // Loại bỏ dấu và chuyển cả hai chuỗi thành chữ thường
        String normalizedSource = removeAccent(source.toLowerCase());
        String normalizedKeyword = removeAccent(keyword.toLowerCase());

        // Kiểm tra nếu chuỗi đã chuẩn hóa có chứa từ khóa
        return normalizedSource.contains(normalizedKeyword);
    }
}

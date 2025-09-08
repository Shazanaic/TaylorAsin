package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Formatter;
import java.util.Locale;

public class Main1 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Введите x (по модулю <= 1): ");
        BigDecimal x = new BigDecimal(br.readLine().trim(), MathContext.DECIMAL128);

        System.out.print("Введите k: ");
        int k = Integer.parseInt(br.readLine().trim());

        if (x.abs().compareTo(BigDecimal.ONE) > 0) {
            System.out.println("Требуется |x| <= 1.");
            return;
        }
        if (k <= 0) {
            System.out.println("k - натуральное число.");
            return;
        }
        BigDecimal e = BigDecimal.ONE.divide(BigDecimal.TEN.pow(k), MathContext.DECIMAL128);

        BigDecimal result = Taylor.arcsin(x, e, k + 5);
        double libresult = Math.asin(x.doubleValue());
        BigDecimal libbig = new BigDecimal(libresult, MathContext.DECIMAL128);
        BigDecimal dif = libbig.subtract(result);

        Formatter fmt = new Formatter(Locale.US);
        BigInteger xi = x.setScale(0, RoundingMode.HALF_UP).toBigInteger();
        fmt.format("Округлённый x = %d, 8 c/c = %o, 16 c/c = %x%n", xi, xi, xi);

        String roflan = "%0+(#20." + (k + 1) + "f%n";
        fmt.format("Результат по Тейлору: " + roflan, result);
        fmt.format("Math.asin(x):        " + roflan, libresult);
        fmt.format("Разница:             " + roflan, dif);

        System.out.print(fmt.toString());
        fmt.close();
    }
}

class Taylor {
    public static BigDecimal arcsin(BigDecimal x, BigDecimal e, int scale) {
        MathContext mc = new MathContext(scale, RoundingMode.HALF_UP);

        BigDecimal num = x;
        BigDecimal sum = x;
        int n = 0;

        while (num.abs().compareTo(e) >= 0) {
            //((2n+1)^2)/((2n+2)(2n+3))
            BigDecimal first = BigDecimal.valueOf(2L * n + 1)
                    .multiply(BigDecimal.valueOf(2L * n + 1));
            BigDecimal second = BigDecimal.valueOf(2L * n + 2)
                    .multiply(BigDecimal.valueOf(2L * n + 3));
            BigDecimal recur = first.divide(second, mc);

            num = num.multiply(recur, mc).multiply(x, mc).multiply(x, mc);
            sum = sum.add(num, mc);

            n++;
        }
        return sum;
    }
}

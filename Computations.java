/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticalgorithm;

import java.util.Random;

/**
 * @author FAkinola
 * this handles computations such as mutation --allelle, gray coding etc
 */
public class Computations {

    /**
     * @param bound picks a random number with the range of the bound
     *              e.g if bound 2, the options are 0 or 1- binary
     *              if bound is 10, 0,1,2,3,4,5,6,7,8 or 9 -symbolic
     * @return a random number
     */
    private Integer aNumber(int bound) {
        return new Random().nextInt(bound);
    }

    /**
     * @param gene
     * @param bound
     * @return allelle; works for both symbolic and binary mutation
     */
    public Integer getRandomAllele(int gene, int bound) {
        int allele = aNumber(bound);
        if (allele == gene) {
            getRandomAllele(gene, bound);
        }
        return allele;
    }

    /**
     * @param i
     * @param j
     * @return uses the logic t
     */
    // and Gray to Binary conversion
    // Helper function to xor
    // two characters
    private int xor_c(char i, char j) {
        return (i == j) ? Character.getNumericValue('0') : Character.getNumericValue('1');
    }

    // Helper function to flip the bit
    private int flip(char c) {
        return (c == '0') ? Character.getNumericValue('1') : Character.getNumericValue('0');
    }

    /**
     * @param binary
     * @param bound
     * @param rastrigin
     * @return gray value of a binary number
     * the MSB of gray code is same as binary code so it wont be tampered with
     * while the rest are computed using xor
     */
    // function to convert binary
    // string to gray string
    ChromosomeSelection binaryToGray(ChromosomeSelection binary, int bound, boolean rastrigin) {
        ChromosomeSelection gray = new ChromosomeSelection(bound, rastrigin);

        gray.setGene(0, binary.getGene(0));

        for (int i = 1; i < binary.geneLength; i++) {

            gray.setGene(i, xor_c(Character.forDigit(binary.getGene(i - 1), 10),
                    Character.forDigit(binary.getGene(i), 10)));
        }

        return gray;
    }

    /**
     * @param binary
     * @return gray
     */
    private String binaryToGray(String binary) {
        String gray = "";
        gray += binary.charAt(0);
        for (int i = 1; i < binary.length(); i++) {
            gray += xor_c(binary.charAt(i - 1), binary.charAt(i));
        }
        return gray;
    }

    /**
     * @param gray
     * @return a binary number
     */
    private String grayToBinary(String gray) {
        String binary = "";
        binary += gray.charAt(0);

        for (int i = 1; i < gray.length(); i++) {
            if (gray.charAt(i) == '0')
                binary += binary.charAt(i - 1);
            else
                binary += flip(binary.charAt(i - 1));

        }

        return binary;
    }

    static String decimalToBinary(int decimal) {
        int i = 0;
        String binary = "";
        while (Math.abs(decimal) > 0) {
            binary += String.valueOf(Math.abs(decimal) % 2);
            i++;
            decimal = decimal / 2;
        }
        return new StringBuilder(binary).reverse().toString();
    }

    String decimalToGray(int decimal) {
        String binary = decimalToBinary(decimal);
        String standardBinaryForm = "";
        for (int i = 0; i < 16 - binary.length(); i++) {
            if (decimal < 0 && i == 0) {
                standardBinaryForm += 1;
                i++;
            }
            standardBinaryForm += 0;
        }
        standardBinaryForm += binary;
        return binaryToGray(standardBinaryForm);
    }

    ChromosomeSelection grayToDecimal(ChromosomeSelection gray) {
        String grayString;

        ChromosomeSelection finalDec = new ChromosomeSelection(8, true);
        for (int j = 0; j < finalDec.geneLength / 16; j++) {
            boolean isNegative = gray.getGene(16 * j) == 1;
            boolean isbeginningOfNo = false;
            grayString = "";
            for (int i = 16 * j + 1; i < 16 * (j + 1); i++) {
                if (gray.getGene(i) != 0 || isbeginningOfNo) {
                    isbeginningOfNo = true;
                    grayString += String.valueOf(gray.getGene(i));
                } else if (i == (16 * j + 15)) {
                    grayString = String.valueOf(gray.getGene(i));
                }
            }
            if (isNegative) {
                finalDec.setGene(j, Integer.parseInt(grayToBinary(grayString), 2) * -1);
            } else {
                finalDec.setGene(j, Integer.parseInt(grayToBinary(grayString), 2));
            }
        }
        return finalDec;
    }

    ChromosomeSelection grayToBinary(ChromosomeSelection gray, int bound) {
        ChromosomeSelection binary = new ChromosomeSelection(bound, false);

        binary.setGene(0, gray.getGene(0));

        for (int i = 1; i < gray.geneLength; i++) {
            if (gray.getGene(i) == Character.getNumericValue('0'))
                binary.setGene(i, binary.getGene(i - 1));

            else {
                binary.setGene(i, flip(Character.forDigit(binary.getGene(i - 1), 10)));
            }
        }
        return binary;
    }
}

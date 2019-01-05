package com.kit.prover.Helpers;

public class ParseBalanceHelper {

    public String getBalanceFromConfirmationLetter(String confirmationLetter) {

        System.out.println("---------------In getBalanceFromConfirmationLetter---------------------");

        int index = subStringIndex(confirmationLetter, "balance");

        StringBuilder sb = new StringBuilder();

        while(true) {
            if(!Character.isDigit(confirmationLetter.charAt(index))) index++;
            else break;
        }

        while (true) {
            if(Character.isDigit(confirmationLetter.charAt(index))) {
                sb.append(confirmationLetter.charAt(index++));
            }
            else {
                break;
            }
        }

        System.out.println("The balance is: "+sb.toString());

        return sb.toString();
    }

    private static int subStringIndex(String str, String substr) {
        int substrlen = substr.length();
        int strlen = str.length();
        int j = 0;

        if (substrlen >= 1) {
            for (int i = 0; i < strlen; i++) {              // iterate through main string
                if (str.charAt(i) == substr.charAt(j)) {    // check substring
                    j++;                                    // iterate
                    if (j == substrlen) {                   // when to stop
                        return i - substrlen; //found substring. As i is currently at the end of our substr so sub substrlen
                    }
                }
                else {
                    j = 0;
                }
            }
        }
        return -1;
    }
}

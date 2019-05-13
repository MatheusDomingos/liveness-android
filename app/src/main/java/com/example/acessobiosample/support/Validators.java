package com.example.acessobiosample.support;

public class Validators {

   public static class CPF {
      private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

      public static boolean isValid(String cpf) {
         if ((cpf == null) || (cpf.length() != 11)) return false;

         switch (cpf) {
            case "00000000000": return false;
            case "11111111111": return false;
            case "22222222222": return false;
            case "33333333333": return false;
            case "44444444444": return false;
            case "55555555555": return false;
            case "66666666666": return false;
            case "77777777777": return false;
            case "88888888888": return false;
            case "99999999999": return false;
         }

         Integer digito1 = calcularDigito(cpf.substring(0, 9), pesoCPF);
         Integer digito2 = calcularDigito(cpf.substring(0, 9) + digito1, pesoCPF);
         return cpf.equals(cpf.substring(0, 9) + digito1.toString() + digito2.toString());
      }
   }

   public static class CNPJ {
      private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
      
      public static boolean isValid(String cnpj) {
         if ((cnpj == null)||(cnpj.length() != 14)) return false;

         Integer digito1 = calcularDigito(cnpj.substring(0, 12), pesoCNPJ);
         Integer digito2 = calcularDigito(cnpj.substring(0, 12) + digito1, pesoCNPJ);
         return cnpj.equals(cnpj.substring(0, 12) + digito1.toString() + digito2.toString());
      }
   }

   private static int calcularDigito(String str, int[] peso) {
      int soma = 0;

      for (int indice = str.length() - 1, digito; indice >= 0; indice--) {
         digito = Integer.parseInt(str.substring(indice, indice + 1));
         soma += digito * peso[peso.length - str.length() + indice];
      }
      soma = 11 - soma % 11;
      return soma > 9 ? 0 : soma;
   }

   public static class BirthDate {
      public static boolean isValid(String birthDate) {
         if (birthDate == null || birthDate.isEmpty()) return false;
         String birthDateSplited[] = birthDate.split("/");
         if (Integer.parseInt(birthDateSplited[2]) < 1900) {
            return false;
         } else if (Integer.parseInt(birthDateSplited[2]) > 2002) {
            return false;
         }

         return true;
      }
   }
}
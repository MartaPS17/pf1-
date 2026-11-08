

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class FiltreLog {
  public static void main(String[] args) {
      String paraulaClau = "ERROR"; 
      
      if (args != null && args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) { 
      paraulaClau = args[0];
      }
      int comptador = 0;
      boolean contingut = false;

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
        String linia;
        while((linia = reader.readLine()) != null){
          contingut = true;

          if(linia.contains(paraulaClau)){
            comptador++;
          }
        }
      } catch (IOException e){
        System.err.println("Error de lectura a la linea " + e.getMessage());
        System.exit(1);
      }

      if (!contingut){
        System.err.println("Error: Text buit");
        System.exit(1);
      }
      System.out.println(comptador);
      System.out.flush();
      System.exit(0);
      
  }
}

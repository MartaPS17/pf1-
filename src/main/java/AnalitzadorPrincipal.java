import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AnalitzadorPrincipal {
    public static void main(String[] args) {
        String textLog = "2026-11-03 10:00:00 [ERROR] Fallada de connexió a la BD\n"
                + "2026-09-13 10:01:00 [WARNING] Memòria RAM alta\n"
                + "2026-09-13 10:02:00 [ERROR] Autenticació fallida\n"
                + "2026-09-13 10:03:00 [WARNING] Disc ple\n"
                + "2026-09-13 10:04:00 [ERROR] Timeout en el servei\n"
                + "2026-09-13 10:07:00 [WARNING] Sessió caducada\n";

        File fitxerErrors = new File("errors_filtre.log");
        String classpath = System.getProperty("java.class.path");

            //bloc 1 = configuració
        ProcessBuilder pbA = new ProcessBuilder("java", "-cp", classpath, "FiltreLog", "ERROR");
        ProcessBuilder pbB = new ProcessBuilder("java", "-cp", classpath, "FiltreLog", "WARNING");
        pbA.redirectError(ProcessBuilder.Redirect.appendTo(fitxerErrors));
        pbB.redirectError(ProcessBuilder.Redirect.appendTo(fitxerErrors));

        //bloc 2 = llançament
        try {
            Process procesA = pbA.start();
            Process procesB = pbB.start();
            byte[] dades = textLog.getBytes(StandardCharsets.UTF_8);
            //bloc 3 = tancament
            try (OutputStream osA = procesA.getOutputStream(); OutputStream osB = procesB.getOutputStream()) {
                osA.write(dades);
                osA.flush();
                osB.write(dades);
                osB.flush();
            }

            int numErrors = llegirResultat(procesA);
            int numWarnings = llegirResultat(procesB);
            int exitCodeA = procesA.waitFor();
            int exitCodeB = procesB.waitFor();
            int exitCodeGlobal = (exitCodeA == 0 && exitCodeB == 0) ? 0 : 1;

            System.out.printf("RESULTAT: ERRORS=%d | WARNINGS=%d | EXIT_CODE=%d%n",
                    numErrors, numWarnings, exitCodeGlobal);
        } catch (IOException e) {
            System.err.println("Error en la comunicació amb els processos: " + e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("S'ha interromput l'espera dels processos.");
            System.exit(1);
        }
    }

    private static int llegirResultat(Process proc) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
            String linia = reader.readLine();
            if (linia != null && !linia.trim().isEmpty()) {
                return Integer.parseInt(linia.trim());
            }
        }
        return 0;
    }
}





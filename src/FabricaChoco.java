import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

import org.json.JSONObject;

public class FabricaChoco implements Produccion{

	public static void main(String[] args) {
		
		try {
			System.out.println("Obteniendo información climatológica, por favor espere...");
			URL url = new URL("https://www.el-tiempo.net/api/json/v2/provincias/28");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			
			int res = conn.getResponseCode();
			if (res != 200) { throw new RuntimeException("HttpResponse: " + res);} 
			else {
				StringBuilder sb = new StringBuilder();
				Scanner sc = new Scanner(url.openStream());
				while (sc.hasNext()) { sb.append(sc.nextLine() + "\n");}
				sc.close();

				JSONObject respuesta = new JSONObject(sb.toString()) ;
				JSONObject temperaturas = respuesta.getJSONArray("ciudades").getJSONObject(0).getJSONObject("temperatures");
				int temperaturaMax = temperaturas.getInt("max");
				System.out.println("Temperatura máxima en Madrid: "+ temperaturaMax + "ºC");
				if (temperaturaMax > 40) {
					System.out.println("Hoy no se producirá chocolate.");
				} else {
					System.out.println("Condiciones aceptables para la producción.");
					System.out.println("Producciendo chocolate...");
					new FabricaChoco().produccionActiva();
					System.out.println("Chocolate producido.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void produccionActiva() {
		LinkedList<Chocolate> chocos = new LinkedList<Chocolate>();
		chocos.add(new Chocolate("Blanco", "Choco", 180, 72 ));
		chocos.add(new Chocolate("Negro", "Choco", 260, 90 ));
		chocos.add(new Chocolate("con almendras", "Choco", 200, 43 ));
		chocos.add(new Chocolate("con castañas de caju", "Choco", 270, 78 ));
		chocos.add(new Chocolate("en rama", "Choco", 226, 76 ));
		chocos.add(new Chocolate("con 70% de cacao", "Choco", 215, 70 ));
		LinkedList<Integer> cantidad = new LinkedList<Integer>();
		cantidad.add(1000);
		cantidad.add(1500);
		cantidad.add(1200);
		cantidad.add(1300);
		cantidad.add(100);
		cantidad.add(1500);
		String prod = "\\tNOMBRE\\t\\t\\t\\t\\t\\t\\t\\tCANTIDAD PRODUCIDA\\n";
		for (int i = 0; i < chocos.size(); i++) {
			// Tabs equalizer
			if (chocos.get(i).nombre.length() < 20) {
				int tabs = 20/4 - chocos.get(i).nombre.length() / 4;
				for (int j = 0; j < tabs; j++) {
					chocos.get(i).nombre+="\\t";
				}
			}
			prod+="- Chocolate " + chocos.get(i).nombre + "\\t\\t\\t\\t" + cantidad.get(i)+"\\n";
		}
		
		try {
			String fecha = LocalDate.now().toString();
			Files.write(Paths.get("salida_"+fecha+".txt"), Arrays.asList(prod.replace("\\t", "\t").replace("\\n", "\n")), StandardCharsets.UTF_8);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ArrayList<String> jenkinsLines = new ArrayList<String>();
		jenkinsLines.add(""
				+ "import java.util.Date\r\n"
				+ "import java.text.SimpleDateFormat\r\n"
				+ "pipeline {\r\n"
				+ "   agent any\r\n"
				+ "   stages {\r\n"
				+ "      stage('Hello') {\r\n"
				+ "         steps {\r\n"
				+ "			  script{\r\n"
				+ "				println \""+prod+"\"\r\n"
				+ "			  }\r\n"
				+ "         }\r\n"
				+ "      }\r\n"
				+ "   }\r\n"
				+ "}\r\n"
				+ "");
		try {
			Path jenkins = Paths.get("Jenkinsfile");
			Files.write(jenkins, jenkinsLines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

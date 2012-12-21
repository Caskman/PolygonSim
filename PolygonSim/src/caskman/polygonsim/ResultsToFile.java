package caskman.polygonsim;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class ResultsToFile implements Results {

	private FileWriter fw;
	private String file = "results.cask";

	public ResultsToFile() {
		try {
			fw = new FileWriter(new File(file));
			fw.close();
		} catch (Exception e) {

		}
	}

	@Override
	public void print(Object o) {
		try {
			fw = new FileWriter(new File(file),true);
			fw.append(o.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
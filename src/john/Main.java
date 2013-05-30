package john;

import java.util.ArrayList;
import java.util.List;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] lx = {.25,.75,.75,.25};
		double[] ly = {.25,.25,.75,.75};
		List<Tower> LT = new ArrayList<Tower>();
		List<Double> Lx = new ArrayList<Double>();
		Lx.addAll(Lx);
		List<Double> Ly = new ArrayList<Double>();
		Ly.addAll(Ly);
		LT.add(new Tower(Lx,Ly));
		SimpleModel SM = new SimpleModel(LT, new Photon());
		SM.run(1);
	}

}

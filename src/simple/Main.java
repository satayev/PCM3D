package simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<Tower> LT = new ArrayList<Tower>();
		List<Double> Lx = Arrays.asList(.25,.75,.75,.25);
		List<Double> Ly = Arrays.asList(.25,.25,.75,.75);
		LT.add(new Tower(Lx,Ly));
		SimpleModel SM = new SimpleModel(LT, new Photon());
		SM.run(1000000);
		SM.p.stat.printAll();
	}

}

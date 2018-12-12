
public class TestPyulear {
	
	public static void main(String[] args) {

		double[] data = new double[300];
		int fs = 10;
		double f0 = 0.3F;
		for (int i = 1; i <= data.length; i++) {
			data[i - 1] = Math.sin(2 * Math.PI * f0 * 1 / fs * i);
			// data[i] = (double)i;
			// System.out.println(data[i]);
		}

		// len 指数据的长度
		int len = 1024;
		int order = 9;
		PSDData psdData = PyulearMatlab.freqMe(data, len, order, fs);
		double[] Pxx = psdData.getPxx();
		double[] w = psdData.getW();
		
		for (int j = 0; j < Pxx.length; j++) {
			System.out.println(Pxx[j]);
		}
		
		for (int j = 0; j < Pxx.length; j++) {
			System.out.println(w[j]);
		}

	}

}

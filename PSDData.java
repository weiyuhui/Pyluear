

public class PSDData {
	
	private double[] pxx;
	private double[] w;
	//private int p;
	
	public PSDData(double[] pxx, double[] w){
		this.pxx = pxx;
		this.w = w;
	}
	
	public double[] getPxx(){
		return pxx;
	}
	public double[] getW(){
		return w;
	}

}

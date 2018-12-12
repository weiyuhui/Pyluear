

public class PyulearMatlab {

	public static PSDData freqMe(double[] data, int len, int p, int fs) {
		// TODO Auto-generated method stub
		// int order = 8;
		boolean isReal = true;

		// Determine the AR model
		// double[] a = new double[p+1];
		double e = 0;
		double[] k = new double[p];
		double[] R = xcoor(data, p);

		for (int i = 0; i < R.length; i++) {
			R[i] = R[i] / data.length;
			// System.out.println(R[i]);
		}
		double[] RR = new double[p + 1];
		for (int i = 0; i < p + 1; i++) {
			RR[i] = R[i + p];
			// System.out.println(RR[i]);
		}
		double[] a = new double[p + 1];
		double[] b = new double[p + 1];
		a[0] = 1;
		b[0] = 1;
		Feval feval = null;
		try {
			feval = Levinson.levinson(RR, -1, true);
		} catch (ARException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		e = feval.getE();
		double[] temp = feval.getA();
		for (int i = 0; i < temp.length; i++) {
			a[i + 1] = temp[i];
			b[i + 1] = 0;
		}

		/*
		 * freqz: [h,w] = freqz(1,a,options.nfft(:),'whole',Fs{:});
		 * options.nfft(:):len Fs:[]
		 */
		// IIR
		Complex[] aa = new Complex[len];
		Complex[] bb = new Complex[len];
		if (a.length < len) {
			for (int i = 0; i < len; i++) {
				if (i < a.length) {
					aa[i] = new Complex(a[i], 0);
					bb[i] = new Complex(b[i], 0);
				} else {
					aa[i] = new Complex(0, 0);
					bb[i] = new Complex(0, 0);
				}
			}
		} else {
			for (int i = 0; i < len; i++) {
				aa[i] = new Complex(a[i], 0);
				bb[i] = new Complex(b[i], 0);
			}
		}
		Complex[] num = fft(bb);
		Complex[] den = fft(aa);
		Complex[] h = new Complex[len];
		double[] Sxx = new double[h.length];
		for (int i = 0; i < num.length; i++) {
			h[i] = num[i].divides(den[i]);
			Sxx[i] = e*Math.pow(h[i].abs(), 2);
		}
		double[] w = freqz_freqvec(len);
		
		/*
		 * Compute the 1-sided or 2-sided PSD [Power/freq], or mean-square [Power].
		 * [Pxx,w,units] = computepsd(Sxx,w,options.range,options.nfft,options.Fs,'psd');
		 * 
		 */
		int select = len/2+1;
		double[] SxxConnect = new double[select];
		double[] Pxx = new double[select];
		double[] wConnect = new double[select];
		
		for(int i = 0; i < select; i++){
			if(i==0){
				SxxConnect[i] = Sxx[i];
			}else if(i == select-1){
				SxxConnect[i] = Sxx[i];
			}else{
				SxxConnect[i] = 2*Sxx[i];
			}
			//System.out.println(w[i]);
			wConnect[i] = w[i]*fs/(2*Math.PI);
			Pxx[i] = SxxConnect[i]/(2*Math.PI);
		}
		
		
		PSDData  psdData = new PSDData(Pxx, wConnect);
		 
//		 for(int i=0;i<10;i++){
//		 System.out.println(Sxx[i]);
//		 }

		return psdData;
	}

	private static double[] selectData(double[] sxx, int beg, int select) {
		// TODO Auto-generated method stub
		
		if(select>sxx.length){
			return null;
		}else{
			double[] result = new double[select];
			for(int i = beg; i < select; i++){
				result[i] = sxx[i];
			}
			
			return result;
		}
		
		
	}

	private static double[] freqz_freqvec(int nfft) {
		// TODO Auto-generated method stub
		double Fs = 2 * Math.PI;
		double deltaF = Fs / nfft;
		double[] w = new double[nfft];
		for (int i = 0; i < nfft; i++) {
			// w[i] = i*(Fs-deltaF)/nfft;
			w[i] = i * deltaF;
			//System.out.println(w[i]);
		}
		if (nfft % 2 == 0) {
			w[nfft / 2] = Fs / 2;
		} else {
			w[(nfft + 1) / 2 - 1] = Fs / 2 - Fs / (2 * nfft);
			w[(nfft + 1) / 2] = Fs / 2 + Fs / (2 * nfft);
		}
		w[nfft - 1] = Fs - Fs / nfft;
		return w;
	}

	private static double[] xcoor(double[] data, int p) {
		// TODO Auto-generated method stub
		int defaultMaxlag = data.length - 1;
		int ceilLog2 = 1;
		for (int i = 1; i < data.length; i++) {
			if (Math.pow(2, i) >= 2 * data.length - 1) {
				// System.out.println(i);
				ceilLog2 = i;
				break;
			}
		}
		int m2 = (int) Math.pow(2, ceilLog2);
		// System.out.println(m2);
		// FFT
		Complex[] dataLog2 = new Complex[m2];
		if (data.length <= m2) {

			for (int i = 0; i < m2; i++) {
				if (i < data.length) {
					dataLog2[i] = new Complex(data[i], 0);
				} else {
					dataLog2[i] = new Complex(0, 0);
				}
			}
		}
		Complex[] X = fft(dataLog2);
		// for (int i = 0; i < 10; i++) {
		// System.out.println(dataLog2[i]);
		// System.out.println(X[i]);
		// }
		Complex[] Cr = new Complex[m2];
		for (int i = 0; i < m2; i++) {
			Cr[i] = new Complex(Math.pow(X[i].abs(), 2), 0);
			// System.out.println(Cr[i]);
		}
		Complex[] c1 = ifft(Cr);
		double[] c = new double[2 * p + 1];
		for (int i = 0; i < c.length; i++) {
			if (i < p) {
				c[i] = c1[m2 - p + i].re();
			} else {
				c[i] = c1[i - p].re();
			}

			// System.out.println(c[i]);
		}

		return c;
	}

	public static Complex[] fft(Complex[] x) {
		int N = x.length;

		// base case
		if (N == 1)
			return new Complex[] { x[0] };

		// radix 2 Cooley-Tukey FFT
		if (N % 2 != 0) {
			throw new RuntimeException("N is not a power of 2");
		}

		// fft of even terms
		Complex[] even = new Complex[N / 2];
		for (int k = 0; k < N / 2; k++) {
			even[k] = x[2 * k];
		}
		Complex[] q = fft(even);

		// fft of odd terms
		Complex[] odd = even; // reuse the array
		for (int k = 0; k < N / 2; k++) {
			odd[k] = x[2 * k + 1];
		}
		Complex[] r = fft(odd);

		// combine
		Complex[] y = new Complex[N];
		for (int k = 0; k < N / 2; k++) {
			double kth = -2 * k * Math.PI / N;
			Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
			y[k] = q[k].plus(wk.times(r[k]));
			y[k + N / 2] = q[k].minus(wk.times(r[k]));
		}
		return y;
	}

	// compute the inverse FFT of x[], assuming its length is a power of 2
	public static Complex[] ifft(Complex[] x) {
		int N = x.length;
		Complex[] y = new Complex[N];

		// take conjugate
		for (int i = 0; i < N; i++) {
			y[i] = x[i].conjugate();
		}

		// compute forward FFT
		y = fft(y);

		// take conjugate again
		for (int i = 0; i < N; i++) {
			y[i] = y[i].conjugate();
		}

		// divide by N
		for (int i = 0; i < N; i++) {
			y[i] = y[i].scale(1.0 / N);
		}

		return y;

	}

}



import org.apache.commons.lang3.ArrayUtils;


public class Levinson {
	/**
	 * Levinson-Durbin recursion
	 * @param r
	 * @param order
	 * @param allow_singularity
	 * @return
	 * a[]:前m个为模型系数，最后一个为预测误差。
	 * @throws ARException
	 * 
	 */
	public static Feval levinson(double[] r, int order, boolean allow_singularity/* default False */) throws ARException {

		double T0 = r[0];
		double[] T = ArrayUtils.subarray(r, 1, r.length);
		int M = T.length;

		if (order == -1)
			M = T.length;
		else if (order > M)
			throw new ARException("Order must be less than size of the input data");
		else
			M = order;

		double[] A = new double[M];
		double[] ref = new double[M];

		double P = T0;

		for (int k = 0; k < M; k++) {
			double save = T[k];
			double temp;

			if (k == 0)
				temp = -save / P;
			else {
				for (int j = 0; j < k; j++)
					save += A[j] * T[k - j - 1];
				temp = -save / P;
			}
			P = P * (1. - Math.pow(temp, 2));
			//System.out.println(P);
			if (P <= 0 && !allow_singularity)
				throw new ARException("ValueError: singular matrix");

			A[k] = temp;
			ref[k] = temp; // save reflection coeff at each step
			if (k == 0)
				continue;

			int khalf = (k + 1) / 2;
			for (int j = 0; j < khalf; j++) {
				int kj = k - j - 1;
				save = A[j];
				A[j] = save + temp * A[kj];
				if (j != kj)
					A[kj] += temp * save;
			}

		}
		
		Feval feval = new Feval(A, P);

		return feval;
	}
}

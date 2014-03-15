package net.sabamiso.android.kuchipaku;

//
//	KalmanFilter - see also: https://gist.github.com/yoggy/2020928
//

public class KalmanFilter {
	double q = 1.0; // process variance
	double r = 2.0; // estimate of measurement variance, change to see effect

	double xhat = 0.0; // a posteriori estimate of x
	double xhatminus;  // a priori estimate of x
	double p = 1.0;    // a posteriori error estimate
	double pminus;     // a priori error estimate
	double kG = 0.0;   // kalman gain

	KalmanFilter() {
	};

	KalmanFilter(double q, double r) {
		q(q);
		r(r);
	}

	void q(double q) {
		this.q = q;
	}

	void r(double r) {
		this.r = r;
	}

	double xhat() {
		return this.xhat;
	}

	void predict() {
		xhatminus = xhat;
		pminus = p + q;
	}

	double correct(double x) {
		kG = pminus / (pminus + r);
		xhat = xhatminus + kG * (x - xhatminus);
		p = (1 - kG) * pminus;
		return xhat;
	}

	double predict_and_correct(double x) {
		predict();
		return correct(x);
	}
}

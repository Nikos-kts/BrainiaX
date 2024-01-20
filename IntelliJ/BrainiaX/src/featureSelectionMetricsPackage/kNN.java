package featureSelectionMetricsPackage;

public class kNN {




    public String  classify(FeatureVector x, FeatureVector[] X) {
        int k = 11;
        int m = 36;
        double w1 = 0.0,w2 = 0.0;
        int counter1 = 0, counter2 = 0;
        double [] ED = new double[m];
        double [] I = new double[k];
        int [] ILabel = new int[k];
        int [] XLabel = new int[m];
        double sum = 0.0, max = 0.0;

        //*************Classification of Input Feature Vector*************


            //***************Euclidean Distance Calculation**************
            for(int i = 0; i < m; i++)
            {
                for(int j = 0; j < 14; j++)
                {
                    sum = sum + Math.pow((X[i].getEntropyVector()[j] - x.getEntropyVector()[j]), 2.0);
                }
                ED[i] = Math.sqrt(sum);
                if(X[i].getLabelClassName().equals("EyesOpened")) XLabel[i] = 1;
                else XLabel[i] = 2;
                sum = 0.0;
            }
            //***********************************************************

            //***************Search of k Nearest Neighbours**************
            for(int i = 0; i < m; i++)
            {
                if(i<k)
                {
                    I[i] = ED[i];
                    ILabel[i] = XLabel[i];
                }
                else
                {
                    for(int j = 0; j < k; j++)
                    {
                        if(max < I[j]) max = I[j];
                    }

                    for(int j = 0; j < k; j++)
                    {
                        if(ED[i] < I[j] && max == I[j])
                        {
                            I[j] = ED[i];
                            ILabel[j] = XLabel[i];
                        }
                    }
                    max = 0.0;
                }
            }
            //**********************************************************

            //**********Counters and Weights Calculation****************
            for(int i = 0; i < k; i++)
            {
                if(ILabel[i] == 1)
                {
                    w1 = w1 + 1.0/I[i];
                    counter1++;
                }
                else
                {
                    w2 = w2 + 1.0/I[i];
                    counter2++;
                }
            }

            //**********************************************************

            if(w1*counter1 > w2*counter2) {
                return "EyesOpened";
            }
            else {
                return "EyesClosed";
            }

    }
}

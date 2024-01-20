package featureSelectionMetricsPackage;

/**
 * Created by Nick on 10/1/2018.
 */
public class FeatureVector {
    private String LabelClassName;
    private double[] EntropyVector;


    public FeatureVector(String labelClassName, double[] data) {

        this.LabelClassName = labelClassName;
        this.EntropyVector = new double[14];

        for (int i = 0; i < 14; i++){
            this.EntropyVector[i] = data[i];
        }
    }

    public String getLabelClassName(){
        return LabelClassName;
    }

    public double[] getEntropyVector() {
        return EntropyVector;
    }
}

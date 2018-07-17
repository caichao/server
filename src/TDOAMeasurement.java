public class TDOAMeasurement implements Cloneable {
    public int anchorIDOne;
    public int anchorIDTwo;
    public float tdoa;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

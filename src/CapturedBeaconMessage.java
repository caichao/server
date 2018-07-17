public class CapturedBeaconMessage implements Cloneable{
    int selfAnchorId;
    int capturedAnchorId;
    int capturedSequence;
    int preambleIndex;
    float speed;
    long looperCounter;

    @Override
    public String toString() {
        return "CapturedBeaconMessage{" +
                "selfAnchorId=" + selfAnchorId +
                ", capturedAnchorId=" + capturedAnchorId +
                ", capturedSequence=" + capturedSequence +
                ", preambleIndex=" + preambleIndex +
                ", speed = " + speed +
                ", looperCounter=" + looperCounter +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

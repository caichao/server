
public class RspHandler {
    private byte[] rsp = null;

    public synchronized boolean handleResponse(byte[] rsp) {
        this.rsp = rsp;
        this.notify();
        return true;
    }

    public synchronized void waitForResponse() {
        /**
         * dead loop wait for the data to return
         */
        while(this.rsp == null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }

        System.out.println(new String(this.rsp));
    }
}
